const auth = require('../../utils/auth')
const storage = require('../../utils/storage')
const { logout } = require('../../api/auth')

Page({
  data: {
    userInfo: null,
    mode: 'SEEKING',
  },

  async onShow() {
    if (typeof this.getTabBar === 'function') {
      const tabIndex = this.data.mode === 'LIVING' ? 3 : 2
      this.getTabBar().init(tabIndex)
    }
    await getApp().globalData.loginReady
    this.setData({
      userInfo: auth.getUserInfo(),
      mode: auth.getMode(),
    })
  },

  /** BOTH 用户模式切换胶囊（SEEKING/LIVING 不显示，见设计方案 §3.1）。
   *  模式切换不影响登录态，仅改本地展示模式（真实推导由后端按合同完成）。 */
  onSwitchMode() {
    const next = this.data.mode === 'SEEKING' ? 'LIVING' : 'SEEKING'
    getApp().globalData.mode = next
    storage.set('mode', next)
    this.setData({ mode: next })
    wx.reLaunch({ url: '/pages/index/index' })
  },

  goAppointments() {
    // 预约列表是 tabBar 页（SEEKING tab2），必须 switchTab
    wx.switchTab({ url: '/pages/appointment/list' })
  },

  goApplications() {
    wx.navigateTo({ url: '/packageA-rental/pages/application/list' })
  },

  goContracts() {
    wx.navigateTo({ url: '/packageA-rental/pages/contract/list' })
  },

  async onLogout() {
    const refreshToken = storage.get('refreshToken')
    if (refreshToken) {
      try {
        await logout(refreshToken)
      } catch (e) {
        // 登出接口失败不阻塞本地清理
      }
    }
    auth.clearLogin()
    getApp().globalData.userInfo = null
    wx.reLaunch({ url: '/pages/login/index' })
  },
})
