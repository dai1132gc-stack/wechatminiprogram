import { request } from '@/utils/request'
import type { StatsOverviewData } from '@/types/api'

/** GET /admin/stats/overview —— 工作台统计卡片（ADMIN） */
export function getStatsOverview() {
  return request<StatsOverviewData>({ url: '/admin/stats/overview' })
}

/** GET /alarms/active/count —— 顶栏铃铛红点（30 秒轮询） */
export function getActiveAlarmCount() {
  return request<{ count: number }>({ url: '/alarms/active/count' })
}
