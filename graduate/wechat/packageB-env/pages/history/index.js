Page({
  data: {},
  /** 本页由 tabBar reLaunch 进入（分包页无 tabBar），提供返回我家入口 */
  goHome() {
    wx.reLaunch({ url: '/pages/dashboard/index' })
  },
})
