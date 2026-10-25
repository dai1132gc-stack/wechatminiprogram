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

/** GET /houses */
export function listHouses(query: HouseQuery) {
  return request<PageData<HouseItem>>({ url: '/houses', method: 'get', params: query })
}
