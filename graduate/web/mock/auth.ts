import { fail, ok, parseBody, type MockItem, type MockOption } from './_util'
import { ACCOUNTS, type MockAccount } from './_mockdb'

/**
 * 认证 mock：账号口令与后端 V3__seed_demo.sql 的演示账号一致。
 * token 形如 mock-access-{role}-{userId}（明文可读，便于 /users/me 解析演示）。
 */

/** 模拟连续失败锁定：内存计数（刷新页面重置，够演示） */
const failCount: Record<string, number> = {}

function tokenPairOf(acc: MockAccount) {
  return {
    accessToken: `mock-access-${acc.role}-${acc.id}`,
    refreshToken: `mock-refresh-${acc.role}-${acc.id}`,
    expiresIn: 7200,
  }
}

function handleLogin(option: MockOption): unknown {
  const { username, password } = parseBody<{ username?: string; password?: string }>(option.body)
  if (!username || !password) {
    return fail(10001, '参数校验失败：账号与密码必填')
  }
  const count = failCount[username] ?? 0
  if (count >= 5) {
    return fail(20005, '登录尝试过于频繁，请 15 分钟后再试')
  }
  const acc = ACCOUNTS.find((a) => a.username === username && a.password === password)
  if (!acc) {
    failCount[username] = count + 1
    return fail(20006, '用户名或密码错误')
  }
  failCount[username] = 0
  return ok({
    ...tokenPairOf(acc),
    userInfo: {
      id: acc.id,
      username: acc.username,
      nickname: acc.nickname,
      avatar: null,
      phone: '138****5678',
      realName: acc.realName,
      role: acc.role,
      orgName: acc.orgName,
      status: 'ACTIVE',
    },
    permissions: acc.permissions,
  })
}

function handleRefresh(option: MockOption): unknown {
  const { refreshToken } = parseBody<{ refreshToken?: string }>(option.body)
  // 非 mock 前缀即视为无效/重放：前端收到 20003 清令牌跳登录
  if (!refreshToken || !refreshToken.startsWith('mock-refresh-')) {
    return fail(20003, '刷新令牌无效，请重新登录')
  }
  const parts = refreshToken.split('-')
  const acc = ACCOUNTS.find((a) => a.role === parts[2] && String(a.id) === parts[3])
  if (!acc) {
    return fail(20003, '刷新令牌无效，请重新登录')
  }
  return ok(tokenPairOf(acc))
}

export default [
  {
    url: '/api/v1/auth/login',
    method: 'post',
    response: handleLogin,
  },
  {
    url: '/api/v1/auth/refresh',
    method: 'post',
    response: handleRefresh,
  },
  {
    url: '/api/v1/auth/logout',
    method: 'post',
    response: () => ok(null),
  },
] satisfies MockItem[]
