import { fail, ok, type MockItem, type MockOption } from './_util'
import { parseAccessHeader } from './_mockdb'

/** 当前用户信息：从 Authorization 头解析 mock token */
function handleMe(option: MockOption): unknown {
  const acc = parseAccessHeader(option.headers)
  if (!acc) {
    return fail(20001, '未登录或令牌缺失')
  }
  return ok({
    id: acc.id,
    username: acc.username,
    nickname: acc.nickname,
    avatar: null,
    phone: '138****5678',
    realName: acc.realName,
    role: acc.role,
    orgName: acc.orgName,
    status: 'ACTIVE',
  })
}

export default [
  { url: '/api/v1/users/me', method: 'get', response: handleMe },
  { url: '/api/v1/users/me', method: 'put', response: handleMe },
] satisfies MockItem[]
