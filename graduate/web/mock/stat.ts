import { fail, ok, type MockItem, type MockOption } from './_util'
import { HOUSES, parseAccessHeader } from './_mockdb'

/** 统计卡片（ADMIN 专属）：LANDLORD 调用返回 30001，验证前端权限分支 */
function handleOverview(option: MockOption): unknown {
  const acc = parseAccessHeader(option.headers)
  if (!acc) return fail(20001, '未登录或令牌缺失')
  if (acc.role !== 'ADMIN') return fail(30001, '无操作权限')

  return ok({
    userTotal: 86,
    houseTotal: HOUSES.length,
    houseAvailable: HOUSES.filter((h) => h.rentStatus === 'AVAILABLE').length,
    housePendingAudit: HOUSES.filter((h) => h.auditStatus === 'PENDING').length,
    contractEffective: HOUSES.filter((h) => h.rentStatus === 'RENTED').length,
    deviceOnline: HOUSES.filter((h) => h.deviceStatus === 'ONLINE').length,
    deviceTotal: HOUSES.filter((h) => h.deviceStatus !== null).length,
    alarmActive: 3,
    newUsers7d: 5,
  })
}

/** 告警红点：按角色给不同数字，演示数据权限过滤 */
function handleAlarmCount(option: MockOption): unknown {
  const acc = parseAccessHeader(option.headers)
  if (!acc) return fail(20001, '未登录或令牌缺失')
  return ok({ count: acc.role === 'ADMIN' ? 3 : 1 })
}

export default [
  { url: '/api/v1/admin/stats/overview', method: 'get', response: handleOverview },
  { url: '/api/v1/alarms/active/count', method: 'get', response: handleAlarmCount },
] satisfies MockItem[]

