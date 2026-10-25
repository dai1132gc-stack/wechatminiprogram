/**
 * 带过期时间的本地存储封装：读时惰性过期，写时可选 ttl。
 */
const PREFIX = 'rental:'

function get(key, defaultValue = null) {
  try {
    const raw = wx.getStorageSync(PREFIX + key)
    if (!raw) return defaultValue
    if (raw.__expire && Date.now() > raw.__expire) {
      wx.removeStorageSync(PREFIX + key)
      return defaultValue
    }
    return raw.value
  } catch (e) {
    return defaultValue
  }
}

function set(key, value, ttlDays) {
  const wrap = { value }
  if (ttlDays) {
    wrap.__expire = Date.now() + ttlDays * 24 * 3600 * 1000
  }
  wx.setStorageSync(PREFIX + key, wrap)
}

function remove(key) {
  wx.removeStorageSync(PREFIX + key)
}

function clearAll() {
  const info = wx.getStorageInfoSync()
  info.keys
    .filter((k) => k.startsWith(PREFIX))
    .forEach((k) => wx.removeStorageSync(k))
}

module.exports = { get, set, remove, clearAll }
