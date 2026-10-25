const storage = require('./storage')
const request = require('./request')

/**
 * 登录态：令牌存取、静默校验、微信登录三件套。
 */

function isLoggedIn() {
  return Boolean(storage.get('accessToken'))
}

function saveLogin(data) {
  storage.set('accessToken', data.accessToken)
  storage.set('refreshToken', data.refreshToken)
  storage.set('userInfo', data.userInfo)
  if (data.mode) storage.set('mode', data.mode)
}

function clearLogin() {
  ;['accessToken', 'refreshToken', 'userInfo', 'mode'].forEach((k) => storage.remove(k))
}

function getUserInfo() {
  return storage.get('userInfo')
}

function getMode() {
  return storage.get('mode') || 'SEEKING'
}

/** 静默校验：app.js 启动时恢复登录态用 */
async function silentCheck() {
  if (!isLoggedIn()) return null
  const envelope = await request.get('/users/me')
  if (envelope.code !== 0 || !envelope.data) return null
  storage.set('userInfo', envelope.data)
  const modeEnvelope = await request.get('/users/me/mode')
  if (modeEnvelope.code === 0 && modeEnvelope.data) {
    storage.set('mode', modeEnvelope.data.mode || 'SEEKING')
  }
  return envelope.data
}

/** 微信一键登录：wx.login 拿 code → POST /auth/wx-login */
function wxLogin() {
  return new Promise((resolve, reject) => {
    wx.login({
      success: (res) => {
        if (!res.code) {
          reject(new Error('20007 微信登录凭证无效'))
          return
        }
        request
          .post('/auth/wx-login', { code: res.code })
          .then((envelope) => {
            if (envelope.code === 0 && envelope.data) {
              saveLogin(envelope.data)
              resolve(envelope.data)
            } else {
              reject(new Error(envelope.message || '登录失败'))
            }
          })
          .catch(reject)
      },
      fail: () => reject(new Error('微信登录调用失败')),
    })
  })
}

module.exports = { isLoggedIn, saveLogin, clearLogin, getUserInfo, getMode, silentCheck, wxLogin }
