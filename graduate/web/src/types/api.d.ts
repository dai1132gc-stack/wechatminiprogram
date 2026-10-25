/**
 * 与 server/docs/openapi.yaml 对齐的核心类型（W1 覆盖 auth / user / house / stats）。
 * 字段命名 camelCase，枚举大写字符串，时间 ISO-8601 带时区。
 */

/** 统一响应体（技术方案 §6.1） */
export interface ApiEnvelope<T = unknown> {
  code: number
  message: string
  data: T | null
  traceId: string
  timestamp: number
}

/** 分页数据体：字段固定 list/total/pageNum/pageSize/pages */
export interface PageData<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

export type RoleType = 'TENANT' | 'LANDLORD' | 'ADMIN'
export type UserMode = 'SEEKING' | 'LIVING' | 'BOTH'
export type AuditStatus = 'PENDING' | 'APPROVED' | 'REJECTED'
export type RentStatus = 'AVAILABLE' | 'LOCKED' | 'RENTED' | 'OFFLINE'
export type DeviceStatus = 'ONLINE' | 'OFFLINE' | 'UNBOUND' | 'FAULT'

export interface UserInfo {
  id: number
  username: string | null
  nickname: string
  avatar: string | null
  phone: string | null
  realName: string | null
  role: RoleType
  orgName: string | null
  status: 'ACTIVE' | 'DISABLED'
}

export interface TokenPair {
  accessToken: string
  refreshToken: string
  /** access token 有效期（秒） */
  expiresIn: number
}

export interface LoginData extends TokenPair {
  userInfo: UserInfo
  permissions: string[]
}

export interface HouseItem {
  id: number
  title: string
  district: string
  address: string
  layout: string
  area: number
  rent: number
  deposit: number
  coverUrl: string | null
  auditStatus: AuditStatus
  rentStatus: RentStatus
  deviceStatus: DeviceStatus | null
  latestHcho: number | null
  hchoExceeded: boolean
  createdAt: string
}

export interface StatsOverviewData {
  userTotal: number
  houseTotal: number
  houseAvailable: number
  housePendingAudit: number
  contractEffective: number
  deviceOnline: number
  deviceTotal: number
  alarmActive: number
  newUsers7d: number
}

/** 分页请求参数 */
export interface PageParams {
  pageNum?: number
  pageSize?: number
}
