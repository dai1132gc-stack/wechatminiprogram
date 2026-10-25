import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { LoginData, RoleType, UserInfo } from '@/types/api'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '@/utils/auth'

/**
 * 用户状态：令牌 + 用户信息 + 权限码。
 * access token 存内存（Pinia）+ sessionStorage 镜像；refresh 只在 localStorage。
 */
export const useUserStore = defineStore('user', () => {
  const accessToken = ref<string>(getAccessToken())
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])

  const roles = computed<RoleType[]>(() => (userInfo.value ? [userInfo.value.role] : []))
  const isLoggedIn = computed(() => accessToken.value.length > 0)

  function setLogin(data: LoginData): void {
    accessToken.value = data.accessToken
    setTokens(data.accessToken, data.refreshToken)
    userInfo.value = data.userInfo
    permissions.value = data.permissions
  }

  function setAccessToken(token: string): void {
    accessToken.value = token
    setTokens(token, getRefreshToken())
  }

  function setUserInfo(info: UserInfo): void {
    userInfo.value = info
  }

  function reset(): void {
    accessToken.value = ''
    userInfo.value = null
    permissions.value = []
    clearTokens()
  }

  /** 按钮级权限：ADMIN 拥有 '*' */
  function hasPermission(code: string): boolean {
    if (permissions.value.includes('*')) return true
    return permissions.value.includes(code)
  }

  return {
    accessToken,
    userInfo,
    permissions,
    roles,
    isLoggedIn,
    setLogin,
    setAccessToken,
    setUserInfo,
    reset,
    hasPermission,
  }
})
