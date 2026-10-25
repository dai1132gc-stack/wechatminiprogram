const { fromHeader, publicInfo } = require('../data/user')

/** /users/me、/users/me/mode：先匹配路由，再做鉴权（无令牌 → 20001） */
function dispatch(ctx, { ok, fail }) {
  if (ctx.path !== '/users/me' && ctx.path !== '/users/me/mode') {
    return null
  }

  const user = fromHeader(ctx.header)
  if (!user) {
    return fail(20001, '未登录或令牌缺失')
  }

  if (ctx.path === '/users/me') {
    return ok(publicInfo(user))
  }
  return ok({
    mode: user.mode,
    contracts:
      user.mode === 'LIVING'
        ? [
            {
              id: 501,
              contractNo: 'HT202609010001',
              houseId: 301,
              houseTitle: '阳光花园 8 栋 132',
              startDate: '2026-09-01',
              endDate: '2027-08-31',
            },
          ]
        : [],
  })
}

module.exports = { dispatch }
