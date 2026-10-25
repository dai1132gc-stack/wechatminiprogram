/** 演示用户：与后端 V3__seed_demo.sql 演示数据对应。token 为明文便于调试。 */

const USERS = [
  {
    id: 10086,
    openid: 'mock-openid-10086',
    username: null,
    nickname: '微信用户_8a2f',
    avatar: '',
    phone: null,
    realName: null,
    role: 'TENANT',
    status: 'ACTIVE',
    mode: 'SEEKING',
    token: 'mock-access-TENANT-10086',
  },
  {
    id: 10087,
    openid: 'mock-openid-10087',
    username: null,
    nickname: '王租客（在租）',
    avatar: '',
    phone: '138****1234',
    realName: null,
    role: 'TENANT',
    status: 'ACTIVE',
    mode: 'LIVING',
    token: 'mock-access-TENANT-10087',
  },
]

/** 从 Authorization 头解析当前用户 */
function fromHeader(header = {}) {
  const key = Object.keys(header).find((k) => k.toLowerCase() === 'authorization')
  if (!key) return null
  const token = String(header[key]).replace(/^Bearer\s+/i, '')
  if (!token.startsWith('mock-access-')) return null
  const parts = token.split('-')
  return USERS.find((u) => u.role === parts[2] && String(u.id) === parts[3]) || null
}

function publicInfo(u) {
  return {
    id: u.id,
    username: u.username,
    nickname: u.nickname,
    avatar: u.avatar || null,
    phone: u.phone,
    realName: u.realName,
    role: u.role,
    orgName: null,
    status: u.status,
  }
}

module.exports = { USERS, fromHeader, publicInfo }
