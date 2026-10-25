const request = require('../utils/request')

/** POST /auth/wx-login（登录主流程见 utils/auth.js wxLogin） */
function wxLogin(code) {
  return request.post('/auth/wx-login', { code })
}

/** POST /auth/logout */
function logout(refreshToken) {
  return request.post('/auth/logout', { refreshToken })
}

/** GET /users/me */
function me() {
  return request.get('/users/me')
}

/** GET /users/me/mode */
function mode() {
  return request.get('/users/me/mode')
}

module.exports = { wxLogin, logout, me, mode }
