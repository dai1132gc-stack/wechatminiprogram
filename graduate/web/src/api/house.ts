import { request } from '@/utils/request'
import type { HouseItem, PageData, PageParams } from '@/types/api'

export interface HouseQuery extends PageParams {
  keyword?: string
  district?: string
  layout?: string
  rentMin?: number
  rentMax?: number
  auditStatus?: string
  rentStatus?: string
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface HouseDetail extends HouseItem {
  street?: string
  lat?: number
  lng?: number
  floor?: number
  totalFloor?: number
  orientation?: string
  decoration?: string
  facilities?: string[]
  imageUrls?: string[]
  description?: string
  auditRemark?: string
  auditTime?: string
  landlord?: { id: number; nickname: string; phone: string } | null
  device?: { id: number; name: string; status: string; sensorProfile: string } | null
  version?: number
}

/** GET /houses */
export function listHouses(query: HouseQuery) {
  return request<PageData<HouseItem>>({ url: '/houses', method: 'get', params: query })
}

/** GET /houses/mine（LANDLORD） */
export function myHouses(query: HouseQuery) {
  return request<PageData<HouseItem>>({ url: '/houses/mine', method: 'get', params: query })
}

/** GET /houses/{id} */
export function getHouse(id: number | string) {
  return request<HouseDetail>({ url: `/houses/${id}` })
}

/** GET /houses/audit/pending（ADMIN） */
export function pendingAuditHouses(params: PageParams) {
  return request<PageData<HouseItem>>({ url: '/houses/audit/pending', method: 'get', params })
}

/** POST /houses/{id}/audit（ADMIN） */
export function auditHouse(id: number, payload: { auditStatus: 'APPROVED' | 'REJECTED'; remark?: string }) {
  return request<null>({ url: `/houses/${id}/audit`, method: 'post', data: payload })
}

/** POST /houses/{id}/offline */
export function offlineHouse(id: number) {
  return request<null>({ url: `/houses/${id}/offline`, method: 'post' })
}

/** POST /houses/{id}/online */
export function onlineHouse(id: number) {
  return request<null>({ url: `/houses/${id}/online`, method: 'post' })
}
