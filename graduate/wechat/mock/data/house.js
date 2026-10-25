/** 演示房源：字段与 openapi.yaml HouseItem 对齐（与 Web 端 mock 同构、数据一致风格）。 */

function seeded(i, salt) {
  const x = Math.sin(i * 127.1 + salt * 311.7) * 43758.5453
  return x - Math.floor(x)
}

const DISTRICTS = ['西湖区', '拱墅区', '余杭区', '滨江区']
const COMMUNITIES = ['阳光花园', '翠苑小区', '文鼎苑', '钱塘府', '桃源居']
const LAYOUTS = ['1-1-1', '2-1-1', '2-2-1', '3-2-2']

const HOUSES = Array.from({ length: 12 }, (_, i) => {
  const community = COMMUNITIES[Math.floor(seeded(i, 2) * COMMUNITIES.length)]
  const building = 1 + Math.floor(seeded(i, 3) * 12)
  const room = 101 + Math.floor(seeded(i, 4) * 20) * 10 + 1
  const rentValue = 1800 + Math.round(seeded(i, 6) * 42) * 100
  const hcho = Number((0.02 + seeded(i, 7) * 0.075).toFixed(3))
  return {
    id: 300 + i + 1,
    title: `${community} ${building} 栋 ${room}`,
    district: DISTRICTS[Math.floor(seeded(i, 1) * DISTRICTS.length)],
    address: `文${Math.floor(seeded(i, 8) * 4) + 1}西路 ${Math.floor(seeded(i, 9) * 200) + 20} 号`,
    layout: LAYOUTS[Math.floor(seeded(i, 5) * LAYOUTS.length)],
    area: Number((45 + seeded(i, 10) * 70).toFixed(1)),
    rent: rentValue,
    deposit: rentValue,
    coverUrl: null,
    auditStatus: 'APPROVED',
    rentStatus: i % 5 === 0 ? 'RENTED' : i % 7 === 0 ? 'LOCKED' : 'AVAILABLE',
    deviceStatus: i % 4 === 0 ? 'OFFLINE' : 'ONLINE',
    latestHcho: hcho,
    hchoExceeded: hcho > 0.08,
    createdAt: new Date(Date.now() - (i * 37 + 20) * 3600 * 1000).toISOString(),
  }
})

module.exports = { HOUSES }
