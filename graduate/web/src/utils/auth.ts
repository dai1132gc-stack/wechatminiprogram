/**
 * 令牌读写（技术方案 §5.1）：
 * access → 内存(user store) + sessionStorage（关标签页即失效）；
 * refresh → localStorage（7 天免登录）；登录账号「记住账号」只记账号不记密码。
 */
const ACCESS_KEY = 'rental_access_token'
const REFRESH_KEY = 'rental_refresh_token'

export function getAccessToken(): string {
  return sessionStorage.getItem(ACCESS_KEY) ?? ''
}

export function getRefreshToken(): string {
  return localStorage.getItem(REFRESH_KEY) ?? ''
}

export function setTokens(accessToken: string, refreshToken: string): void {
  sessionStorage.setItem(ACCESS_KEY, accessToken)
  localStorage.setItem(REFRESH_KEY, refreshToken)
}

export function clearTokens(): void {
  sessionStorage.removeItem(ACCESS_KEY)
  localStorage.removeItem(REFRESH_KEY)
}

/** 记住账号：只存账号，永不存密码 */
const REMEMBER_KEY = 'rental_remember_username'

export function getRememberedUsername(): string {
  return localStorage.getItem(REMEMBER_KEY) ?? ''
}

export function setRememberedUsername(username: string): void {
  localStorage.setItem(REMEMBER_KEY, username)
}

export function removeRememberedUsername(): void {
  localStorage.removeItem(REMEMBER_KEY)
}
