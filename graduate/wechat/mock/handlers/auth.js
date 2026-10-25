const { USERS, publicInfo } = require('../data/user')

/**
 * /auth/wx-login、/auth/refresh、/auth/logout。
 * wx-login：演示账号轮换 —— 第一次登录给 SEEKING 用户，之后交替 LIVING（便于演示双模式）。
 */
let loginCursor = 0

function tokenPairOf(u) {
  return {
    accessToken: `mock-access-${u.role}-${u.id}`,
    refreshToken: `mock-refresh-${u.role}-${u.id}`,
    expiresIn: 7200,
  }
}

function dispatch(ctx, { ok, fail }) {
  if (ctx.path !== '/auth/wx-login' && ctx.path !== '/auth/refresh' && ctx.path !== '/auth/logout') {
    return null
  }

  if (ctx.path === '/auth/wx-login') {
    if (!ctx.body.code) return fail(20007, '微信登录凭证无效')
    const user = USERS[loginCursor % USERS.length]
    loginCursor += 1
    return ok({ ...tokenPairOf(user), userInfo: publicInfo(user), mode: user.mode })
  }

  if (ctx.path === '/auth/refresh') {
    const token = ctx.body.refreshToken || ''
    if (!token.startsWith('mock-refresh-')) return fail(20003, '刷新令牌无效，请重新登录')
    const parts = token.split('-')
    const user = USERS.find((u) => u.role === parts[2] && String(u.id) === parts[3])
    if (!user) return fail(20003, '刷新令牌无效，请重新登录')
    return ok(tokenPairOf(user))
  }

  // /auth/logout
  return ok(null)
}

module.exports = { dispatch }
