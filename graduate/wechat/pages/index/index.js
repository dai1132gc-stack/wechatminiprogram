const api = require('../../api/house')
const format = require('../../utils/format')

Page({
  data: {
    mode: 'SEEKING',
    houses: [],
    loading: true,
    contract: null,
  },

  async onLoad() {
    // 等启动期登录态恢复完成再取数据，避免读到空全局状态
    await getApp().globalData.loginReady
    await Promise.all([this.loadMode(), this.loadData()])
  },

  onShow() {
    // 必须刷新 tabBar 高亮（设计方案 §3.1 必踩的坑 #2）
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().init(0)
    }
  },

  async loadMode() {
    const app = getApp()
    this.setData({ mode: app.globalData.mode })
  },

  async loadData() {
    if (this.data.mode !== 'SEEKING' && this.data.mode !== 'BOTH') {
      this.setData({ loading: false })
      return
    }
    this.setData({ loading: true })
    try {
      const envelope = await api.listHouses({ pageNum: 1, pageSize: 10 })
      if (envelope.code === 0 && envelope.data) {
        this.setData({
          houses: envelope.data.list.map((h) => ({
            ...h,
            rentText: format.rent(h.rent),
            areaText: format.area(h.area),
            rentStatusText: format.statusText('rentStatus', h.rentStatus),
          })),
        })
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh())
  },

  goHouseList() {
    wx.navigateTo({ url: '/pages/house/list' })
  },

  goHouseDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/house/detail?id=${id}` })
  },
})
