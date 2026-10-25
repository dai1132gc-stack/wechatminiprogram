import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'
import { request } from '@/utils/request'
import type { ApiEnvelope, LoginData, TokenPair } from '@/types/api'

/**
 * 登录接口返回完整信封：登录页需要按 code 分支（20005 禁用按钮 15 分钟、
 * 20006 提示账号或密码错误），不能在拦截器里被吞掉。
 */
export async function login(payload: { username: string; password: string }): Promise<ApiEnvelope<LoginData>> {
  const { data } = await axios.post<ApiEnvelope<LoginData>>(
    `${import.meta.env.VITE_API_BASE_URL}/auth/login`,
    payload,
  )
  return data
}

/** POST /auth/refresh（request.ts 内部已调用，导出备用） */
export function refresh(payload: { refreshToken: string }, config?: AxiosRequestConfig) {
  return request<TokenPair>({ url: '/auth/refresh', method: 'post', data: payload, ...config })
}

/** POST /auth/logout */
export function logout(payload: { refreshToken: string }) {
  return request<null>({ url: '/auth/logout', method: 'post', data: payload })
}
