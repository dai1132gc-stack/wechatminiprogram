const config = require('../config/index')
const storage = require('./storage')

/**
 * wx.request 封装（技术方案 §2.3 / §5.6）：
 * - Promise 化；令牌注入 Authorization: Bearer
 * - code=0 → resolve(data)；20002 → 静默刷新后重放一次；20001/20003 → 清令牌 reLaunch 登录页
 *   （不能用 navigateTo，页面栈会越堆越深）
 * - Mock 与真实请求走同一出口：切换只改 config/env.js 的 useMock 布尔值
 */

const TOKEN_REFRESHING = { flag: false }

function buildHeader() {
  const header = { 'Content-Type': 'application/json' }
  const token = storage.get('accessToken')
  if (token) header.Authorization = `Bearer ${token}`
  return header
}

function rawRequest(method, url, data) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: config.apiBase + url,
      method,
      data,
      header: buildHeader(),
      timeout: 15000,
      success: (res) => resolve(res),
      fail: (err) => reject(err),
    })
  })
}

/** 刷新令牌：一次性轮换；失败清登录态 */
async function refreshTokens() {
  if (TOKEN_REFRESHING.flag) return false
  TOKEN_REFRESHING.flag = true
  try {
    const refreshToken = storage.get('refreshToken')
    if (!refreshToken) return false
    const envelope = await mockOrReal('POST', '/auth/refresh', { refreshToken })
    if (envelope.code !== 0 || !envelope.data) return false
    storage.set('accessToken', envelope.data.accessToken)
    storage.set('refreshToken', envelope.data.refreshToken)
    return true
  } catch (e) {
    return false
  } finally {
    TOKEN_REFRESHING.flag = false
  }
}

function forceRelogin() {
  storage.remove('accessToken')
  storage.remove('refreshToken')
  wx.reLaunch({ url: '/pages/login/index' })
}

async function mockOrReal(method, url, data) {
  if (config.useMock) {
    const mock = require('../mock/index')
    return mock.dispatch(method, url, data, buildHeader())
  }
  const res = await rawRequest(method, url, data)
  return res.data
}

/**
 * 业务请求入口：resolve 整个信封（部分页面需要 code 分支）。
 * 常用写法：const envelope = await request.get('/users/me')；request.ensureData(envelope)
 */
async function request(method, url, data, retried = false) {
  let envelope
  try {
    envelope = await mockOrReal(method, url, data)
  } catch (e) {
    wx.showToast({ title: '网络异常，请重试', icon: 'none' })
    throw e
  }
  if (envelope.code === 0) return envelope

  // 令牌过期：静默刷新一次后重放
  if (envelope.code === 20002 && !retried) {
    const ok = await refreshTokens()
    if (ok) return request(method, url, data, true)
    forceRelogin()
    throw new Error(envelope.message)
  }
  // 未登录 / 刷新失效 / 账号禁用：强制回登录页
  if (envelope.code === 20001 || envelope.code === 20003 || envelope.code === 20004) {
    forceRelogin()
    throw new Error(envelope.message)
  }
  // 其余业务错误（30001/30003 租约失效等不跳登录，由页面处理）
  return envelope
}

const api = {
  get: (url, data) => request('GET', url, data),
  post: (url, data) => request('POST', url, data),
  put: (url, data) => request('PUT', url, data),
}

module.exports = api
