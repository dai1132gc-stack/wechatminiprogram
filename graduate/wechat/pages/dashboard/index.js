const format = require('../../utils/format')

Page({
  data: {
    houseTitle: '',
    metrics: [],
    deviceStatus: 'ONLINE',
    updatedAt: '',
  },

  async onShow() {
    if (typeof this.getTabBar === 'function') {
      const mode = getApp().globalData.mode
      // LIVING 模式高亮 tab1（我家）；SEEKING 用户误入时不高亮
      this.getTabBar().init(mode === 'SEEKING' ? -1 : 0)
    }
    await getApp().globalData.loginReady
    this.loadRealtime()
  },

  /** W1：占位读数（W5 接 /env/houses/{id}/realtime + WebSocket 推送） */
  loadRealtime() {
    const record = { temperature: 26.8, humidity: 58.3, hcho: 0.032, tvoc: 0.42, co2: 780 }
    this.setData({
      houseTitle: '阳光花园 8 栋 132',
      deviceStatus: 'ONLINE',
      updatedAt: format.dateTimeShort(new Date().toISOString()),
      metrics: [
        { key: 'temperature', label: '温度', value: format.metric('temperature', record.temperature), ok: true },
        { key: 'humidity', label: '湿度', value: format.metric('humidity', record.humidity), ok: true },
        { key: 'hcho', label: '甲醛（估算）', value: format.metric('hcho', record.hcho), ok: true },
        { key: 'tvoc', label: 'TVOC', value: format.metric('tvoc', record.tvoc), ok: true },
        { key: 'co2', label: 'CO₂（估算）', value: format.metric('co2', record.co2), ok: true },
      ],
    })
  },

  goHistory() {
    wx.reLaunch({ url: '/packageB-env/pages/history/index' })
  },

  goAlarm() {
    wx.reLaunch({ url: '/packageB-env/pages/alarm/list' })
  },

  goDevice() {
    wx.navigateTo({ url: '/packageB-env/pages/device/info' })
  },
})
