import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiEnvelope, TokenPair } from '@/types/api'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '@/utils/auth'

/**
 * Axios 实例 + 拦截器（技术方案 §5.6）：
 * - 请求：注入 Authorization、X-Trace-Id，GET 加时间戳防缓存
 * - 响应：code=0 → resolve(data)；20002 → 静默刷新后重放；20003 → 清令牌跳登录；
 *         30001~30004 不跳登录；401 走刷新；403 提示无权限；429 提示频繁；5xx 提示 traceId
 */

type EnvelopeRequestConfig = InternalAxiosRequestConfig & { _retried?: boolean }

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
})

let refreshing: Promise<string> | null = null
/** 并发 401/20002 时的挂起队列：刷新成功统一重放，失败统一跳登录 */
const pendingQueue: Array<{
  resolve: (token: string) => void
  reject: (error: unknown) => void
}> = []

function redirectToLogin(): void {
  clearTokens()
  // 不用 router 实例（避免循环依赖）；刷新失败统一回登录页
  const redirect = encodeURIComponent(location.pathname + location.search)
  if (!location.pathname.startsWith('/login')) {
    location.href = `/login?redirect=${redirect}`
  }
}

async function refreshToken(): Promise<string> {
  const refreshTokenValue = getRefreshToken()
  if (!refreshTokenValue) {
    throw new Error('no refresh token')
  }
  // 用独立 axios 调刷新接口，避免走本实例的请求拦截器造成死循环
  const { data: envelope } = await axios.post<ApiEnvelope<TokenPair>>(
    `${import.meta.env.VITE_API_BASE_URL}/auth/refresh`,
    { refreshToken: refreshTokenValue },
  )
  if (envelope.code !== 0 || !envelope.data) {
    throw new Error(envelope.message)
  }
  setTokens(envelope.data.accessToken, envelope.data.refreshToken)
  return envelope.data.accessToken
}

function enqueueRefresh(): Promise<string> {
  if (refreshing) {
    // 已有刷新在途：挂起等待，成功后拿到新令牌重放
    return refreshing
  }
  refreshing = refreshToken()
    .then((token) => {
      pendingQueue.forEach(({ resolve }) => resolve(token))
      pendingQueue.length = 0
      return token
    })
    .catch((error) => {
      pendingQueue.forEach(({ reject }) => reject(error))
      pendingQueue.length = 0
      redirectToLogin()
      ElMessage.error('登录已过期，请重新登录')
      throw error
    })
    .finally(() => {
      refreshing = null
    })
  return refreshing
}

service.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  if (config.method === 'get') {
    config.params = { ...config.params, _t: Date.now() }
  }
  return config
})

service.interceptors.response.use(
  async (response) => {
    const envelope = response.data as ApiEnvelope
    // 非统一响应体（如文件流）直接放行
    if (envelope === null || typeof envelope !== 'object' || typeof envelope.code !== 'number') {
      return response
    }
    if (envelope.code === 0) {
      return response
    }
    const config = response.config as EnvelopeRequestConfig
    // 令牌过期：静默刷新一次后重放原请求
    if (envelope.code === 20002 && !config._retried) {
      config._retried = true
      const token = await enqueueRefresh()
      config.headers.Authorization = `Bearer ${token}`
      return service.request(config)
    }
    // 刷新令牌失效 / 未登录：强制重新登录
    if (envelope.code === 20001 || envelope.code === 20003 || envelope.code === 20004) {
      redirectToLogin()
      if (envelope.code === 20004) {
        ElMessage.error('账号已被禁用，请联系管理员')
      }
      return Promise.reject(new Error(envelope.message))
    }
    // 其余业务错误：toast 后端 message（30003 租约失效等不跳登录）
    ElMessage.error(envelope.message)
    return Promise.reject(new Error(envelope.message))
  },
  async (error: AxiosError<ApiEnvelope>) => {
    const status = error.response?.status
    const config = error.config as EnvelopeRequestConfig | undefined
    const envelope = error.response?.data

    if (status === 401 && config && !config._retried) {
      config._retried = true
      try {
        const token = await enqueueRefresh()
        config.headers.Authorization = `Bearer ${token}`
        return service.request(config)
      } catch {
        return Promise.reject(error)
      }
    }
    if (status === 403) {
      ElMessage.error(envelope?.message ?? '无操作权限')
      return Promise.reject(error)
    }
    if (status === 429) {
      ElMessage.error(envelope?.message ?? '操作过于频繁，请稍后重试')
      return Promise.reject(error)
    }
    if (status !== undefined && status >= 500) {
      ElMessage.error(`服务异常，请稍后重试（traceId: ${envelope?.traceId ?? '-'}）`)
      console.error('[server error]', envelope)
      return Promise.reject(error)
    }
    ElMessage.error(envelope?.message ?? error.message ?? '网络异常')
    return Promise.reject(error)
  },
)

/** 业务请求：直接返回 data（可能为 null），业务码非 0 已在拦截器统一处理 */
export async function request<T>(config: AxiosRequestConfig): Promise<T | null> {
  const { data } = await service.request<ApiEnvelope<T>>(config)
  return (data as ApiEnvelope<T>).data ?? null
}

export default service
