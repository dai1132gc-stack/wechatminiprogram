const { HOUSES } = require('../data/house')

/** /houses 列表（分页 + 关键词筛选） */
function dispatch(ctx, { ok }) {
  if (ctx.path !== '/houses') return null

  let rows = HOUSES
  if (ctx.query.keyword) {
    const kw = ctx.query.keyword.toLowerCase()
    rows = rows.filter((h) => h.title.toLowerCase().includes(kw) || h.address.toLowerCase().includes(kw))
  }
  if (ctx.query.rentStatus) {
    rows = rows.filter((h) => h.rentStatus === ctx.query.rentStatus)
  }

  const pageNum = Number(ctx.query.pageNum || 1)
  const pageSize = Math.min(Number(ctx.query.pageSize || 10), 100)
  const start = (pageNum - 1) * pageSize
  const list = rows.slice(start, start + pageSize)

  return ok({
    list,
    total: rows.length,
    pageNum,
    pageSize,
    pages: Math.max(1, Math.ceil(rows.length / pageSize)),
  })
}

module.exports = { dispatch }
