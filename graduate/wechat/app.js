const auth = require('./utils/auth')
const userStore = require('./store/user')

App({
  globalData: {
    userInfo: null,
    mode: 'SEEKING', // SEEKING | LIVING | BOTH（技术方案 §11 双模式）
    loginReady: null, // Promise：启动期登录态恢复完成后 resolve，避免页面读到空状态
  },

  onLaunch() {
    this.globalData.loginReady = this.restoreLogin()
  },

  /** 启动流程（设计方案 §3.3.1）：读本地 token → 静默校验 → 拉 mode；失败停登录页 */
  async restoreLogin() {
    if (!auth.isLoggedIn()) {
      return { loggedIn: false }
    }
    try {
      const userInfo = await auth.silentCheck()
      if (!userInfo) {
        auth.clearLogin()
        return { loggedIn: false }
      }
      this.globalData.userInfo = userInfo
      this.globalData.mode = auth.getMode()
      userStore.setUser(userInfo)
      userStore.setMode(this.globalData.mode)
      return { loggedIn: true }
    } catch (e) {
      // 静默失败：留在登录页，由用户手动重试（避免启动死循环）
      return { loggedIn: false }
    }
  },
})
