/**
 * Mock 共享数据源：账号、房源。字段与 openapi.yaml HouseItem/UserInfo 一致。
 */

export interface MockAccount {
  id: number
  username: string
  password: string
  nickname: string
  realName: string
  role: 'ADMIN' | 'LANDLORD'
  orgName: string | null
  permissions: string[]
}

export const ACCOUNTS: MockAccount[] = [
  {
    id: 1,
    username: 'admin',
    password: 'admin123',
    nickname: '张管理',
    realName: '张管理',
    role: 'ADMIN',
    orgName: null,
    permissions: ['*'],
  },
  {
    id: 2,
    username: 'landlord1',
    password: '123456',
    nickname: '李房东',
    realName: '李明',
    role: 'LANDLORD',
    orgName: '示例房产中介',
    permissions: ['house:create', 'house:update', 'application:approve', 'device:bind', 'alarm:handle'],
  },
]

/** 从 headers 中取 Authorization（Node 中间件 header 名为小写，做不敏感匹配） */
export function parseAccessToken(headers?: Record<string, string>): string | null {
  if (!headers) return null
  const key = Object.keys(headers).find((k) => k.toLowerCase() === 'authorization')
  const value = key ? headers[key] : null
  if (!value) return null
  return value.replace(/^Bearer\s+/i, '')
}

/** 解析 mock-access-{role}-{id} 形式的明文 token（便于演示调试） */
export function parseAccessHeader(headers?: Record<string, string>): MockAccount | null {
  const token = parseAccessToken(headers)
  if (!token || !token.startsWith('mock-access-')) return null
  const parts = token.split('-')
  return ACCOUNTS.find((a) => a.role === parts[2] && String(a.id) === parts[3]) ?? null
}

export interface MockHouse {
  id: number
  title: string
  district: string
  address: string
  layout: string
  area: number
  rent: number
  deposit: number
  coverUrl: string | null
  auditStatus: 'PENDING' | 'APPROVED' | 'REJECTED'
  rentStatus: 'AVAILABLE' | 'LOCKED' | 'RENTED' | 'OFFLINE'
  deviceStatus: 'ONLINE' | 'OFFLINE' | 'UNBOUND' | 'FAULT' | null
  latestHcho: number | null
  hchoExceeded: boolean
  createdAt: string
}

/** 确定性伪随机（种子固定，每次刷新数据一致，避免表格闪烁） */
function seeded(i: number, salt: number): number {
  const x = Math.sin(i * 127.1 + salt * 311.7) * 43758.5453
  return x - Math.floor(x)
}

const DISTRICTS = ['西湖区', '拱墅区', '余杭区', '滨江区']
const COMMUNITIES = ['阳光花园', '翠苑小区', '文鼎苑', '钱塘府', '桃源居']
const LAYOUTS = ['1-1-1', '2-1-1', '2-2-1', '3-2-2']
const AUDIT_CYCLE: MockHouse['auditStatus'][] = ['APPROVED', 'APPROVED', 'APPROVED', 'PENDING', 'REJECTED', 'APPROVED']
const RENT_CYCLE: MockHouse['rentStatus'][] = ['AVAILABLE', 'AVAILABLE', 'RENTED', 'LOCKED', 'AVAILABLE', 'OFFLINE']
const DEVICE_CYCLE: MockHouse['deviceStatus'][] = ['ONLINE', 'ONLINE', 'OFFLINE', 'ONLINE', null, 'UNBOUND']

export const HOUSES: MockHouse[] = Array.from({ length: 23 }, (_, i) => {
  const id = 300 + i + 1
  const district = DISTRICTS[Math.floor(seeded(i, 1) * DISTRICTS.length)]
  const community = COMMUNITIES[Math.floor(seeded(i, 2) * COMMUNITIES.length)]
  const building = 1 + Math.floor(seeded(i, 3) * 12)
  const room = 101 + Math.floor(seeded(i, 4) * 20) * 10 + 1
  const layout = LAYOUTS[Math.floor(seeded(i, 5) * LAYOUTS.length)]
  const rent = 1800 + Math.round(seeded(i, 6) * 42) * 100
  const device = DEVICE_CYCLE[i % DEVICE_CYCLE.length]
  // 有设备才有甲醛读数；少量超标（> 0.08）供 UI 演示红色分支
  const hasDevice = device !== null && device !== 'UNBOUND'
  const hcho = hasDevice ? Number((0.02 + seeded(i, 7) * 0.075).toFixed(3)) : null
  return {
    id,
    title: `${community} ${building} 栋 ${room}`,
    district,
    address: `文${Math.floor(seeded(i, 8) * 4) + 1}西路 ${Math.floor(seeded(i, 9) * 200) + 20} 号`,
    layout,
    area: Number((45 + seeded(i, 10) * 70).toFixed(1)),
    rent,
    deposit: rent,
    coverUrl: null,
    auditStatus: AUDIT_CYCLE[i % AUDIT_CYCLE.length],
    rentStatus: RENT_CYCLE[i % RENT_CYCLE.length],
    deviceStatus: device,
    latestHcho: hcho,
    hchoExceeded: hcho !== null && hcho > 0.08,
    createdAt: new Date(Date.now() - (i * 37 + 20) * 3600_000).toISOString().replace('Z', '+08:00'),
  }
})
