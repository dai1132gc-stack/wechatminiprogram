import { request } from '@/utils/request'
import type { UserInfo } from '@/types/api'

/** GET /users/me */
export function getCurrentUser() {
  return request<UserInfo>({ url: '/users/me' })
}

/** PUT /users/me */
export function updateCurrentUser(payload: { nickname?: string; avatar?: string; realName?: string }) {
  return request<UserInfo>({ url: '/users/me', method: 'put', data: payload })
}

/** GET /users/me/mode */
export function getCurrentMode() {
  return request<{ mode: string; contracts: unknown[] }>({ url: '/users/me/mode' })
}
