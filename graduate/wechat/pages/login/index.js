const auth = require('../../utils/auth')

Page({
  data: {
    agreed: false,
    loading: false,
  },

  onToggleAgree() {
    this.setData({ agreed: !this.data.agreed })
  },

  /** 微信一键登录：不勾协议不能登录（设计方案 §3.3.1） */
  async handleWxLogin() {
    if (!this.data.agreed) {
      wx.showToast({ title: '请先阅读并同意用户协议', icon: 'none' })
      return
    }
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      const data = await auth.wxLogin()
      const app = getApp()
      app.globalData.userInfo = data.userInfo
      app.globalData.mode = data.mode || 'SEEKING'
      wx.showToast({ title: `欢迎，${data.userInfo.nickname}`, icon: 'none' })
      // 登录成功清空页面栈进首页（不能 navigateTo）
      wx.reLaunch({ url: '/pages/index/index' })
    } catch (e) {
      // 20007 凭证无效 → 提示重试；网络错误不自动重试（避免死循环）
      wx.showToast({ title: e.message || '登录失败，请重试', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  handleLandlordHint() {
    wx.showModal({
      title: '我是房东 / 中介',
      content: '小程序面向租客；房源发布与审核请使用 Web 管理后台（admin 账号登录）。',
      showCancel: false,
    })
  },
})
