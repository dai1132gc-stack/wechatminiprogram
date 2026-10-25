import { injectedError, ok, slicePage, type MockItem, type MockOption } from './_util'
import { HOUSES } from './_mockdb'

/**
 * 房源列表 mock：支持分页 + 关键词/状态筛选。
 * 演示异常注入：?mock=error → 90001（验证页面错误态）。
 */
function handleList(option: MockOption): unknown {
  const injected = injectedError(option)
  if (injected) return injected

  const q = option.query
  let rows = HOUSES
  if (q.keyword) {
    const kw = q.keyword.toLowerCase()
    rows = rows.filter(
      (h) => h.title.toLowerCase().includes(kw) || h.address.toLowerCase().includes(kw),
    )
  }
  if (q.rentStatus) {
    rows = rows.filter((h) => h.rentStatus === q.rentStatus)
  }
  if (q.auditStatus) {
    rows = rows.filter((h) => h.auditStatus === q.auditStatus)
  }
  const pageNum = Number(q.pageNum ?? 1)
  const pageSize = Math.min(Number(q.pageSize ?? 10), 100)
  return ok(slicePage(rows, pageNum, pageSize))
}

export default [
  { url: '/api/v1/houses', method: 'get', response: handleList },
] satisfies MockItem[]
