/**
 * 轻量 observable 用户状态：getUser/setUser 之外，subscribe 登记回调，
 * setData 由页面自己完成（原生小程序不引 MobX，几十行足够，技术方案 §2.3）。
 */
const state = {
  userInfo: null,
  mode: 'SEEKING', // SEEKING | LIVING | BOTH
}

const listeners = []

function getUser() {
  return state.userInfo
}

function getMode() {
  return state.mode
}

function setUser(userInfo) {
  state.userInfo = userInfo
  notify()
}

function setMode(mode) {
  state.mode = mode
  notify()
}

/** BOTH 用户切换展示模式：仅影响本地渲染，不改后端状态 */
function toggleDisplayMode() {
  if (state.mode === 'BOTH') return state.mode
  state.mode = state.mode === 'SEEKING' ? 'LIVING' : 'SEEKING'
  notify()
}

function subscribe(fn) {
  listeners.push(fn)
  return () => {
    const i = listeners.indexOf(fn)
    if (i >= 0) listeners.splice(i, 1)
  }
}

function notify() {
  listeners.forEach((fn) => {
    try {
      fn({ ...state })
    } catch (e) {
      // 单个订阅者异常不影响其他
    }
  })
}

module.exports = { getUser, getMode, setUser, setMode, toggleDisplayMode, subscribe }
