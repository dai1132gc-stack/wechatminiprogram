/**
 * 时间、数值、单位格式化（设计方案 §1.2 / §1.3）：
 * 环境数值甲醛/TVOC 三位小数、CO2 整数；金额取整展示；列表时间 YYYY-MM-DD HH:mm。
 */

function pad(n) {
  return String(n).padStart(2, '0')
}

/** ISO 字符串 → '2026-10-25 14:05'（列表列格式） */
function dateTimeShort(iso) {
  if (!iso) return '-'
  const d = new Date(iso)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 金额：元，整数展示（数据库两位小数） */
function rent(value) {
  return `￥${Math.round(Number(value || 0))}`
}

/** 面积：一位小数 m² */
function area(value) {
  return `${Number(value || 0).toFixed(1)} m²`
}

/** 环境指标格式化：hcho/tvoc 三位小数，co2 整数，温湿度一位小数 */
function metric(metricType, value) {
  if (value === null || value === undefined) return '-'
  const digits = metricType === 'co2' ? 0 : metricType === 'hcho' || metricType === 'tvoc' ? 3 : 1
  const unit = { hcho: 'mg/m³', tvoc: 'mg/m³', co2: 'ppm', temperature: '℃', humidity: '%RH' }[metricType] || ''
  return `${Number(value).toFixed(digits)} ${unit}`
}

const STATUS_TEXT = {
  auditStatus: { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' },
  rentStatus: { AVAILABLE: '可租', LOCKED: '签约中', RENTED: '在租', OFFLINE: '已下架' },
  contractStatus: {
    DRAFT: '草稿', PENDING_SIGN: '待签署', EFFECTIVE: '生效中',
    EXPIRED: '已到期', TERMINATED: '已退租', CANCELLED: '已取消',
  },
  applicationStatus: { SUBMITTED: '待处理', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' },
  appointmentStatus: { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELLED: '已取消' },
}

function statusText(kind, value) {
  return (STATUS_TEXT[kind] && STATUS_TEXT[kind][value]) || value || '-'
}

module.exports = { dateTimeShort, rent, area, metric, statusText }
