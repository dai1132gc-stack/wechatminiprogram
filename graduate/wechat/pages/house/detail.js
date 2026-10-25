const api = require('../../api/house')
const format = require('../../utils/format')

Page({
  data: {
    house: null,
    loading: true,
  },

  onLoad(query) {
    this.houseId = query.id
    this.fetchDetail()
  },

  async fetchDetail() {
    this.setData({ loading: true })
    try {
      const envelope = await api.getHouse(this.houseId)
      if (envelope.code === 0 && envelope.data) {
        const h = envelope.data
        this.setData({
          house: {
            ...h,
            rentText: format.rent(h.rent),
            depositText: format.rent(h.deposit),
            areaText: format.area(h.area),
            rentStatusText: format.statusText('rentStatus', h.rentStatus),
            hchoText: h.latestHcho != null ? format.metric('hcho', h.latestHcho) : null,
            createdText: format.dateTimeShort(h.createdAt),
          },
        })
      } else {
        wx.showToast({ title: envelope.message || '房源不存在', icon: 'none' })
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  /** 提交看房预约（packageA，W3 实现完整表单） */
  goAppointment() {
    if (this.data.house.rentStatus !== 'AVAILABLE') {
      wx.showToast({ title: '该房源当前不可预约', icon: 'none' })
      return
    }
    wx.navigateTo({ url: `/packageA-rental/pages/appointment/create?houseId=${this.houseId}` })
  },

  /** 提交租赁申请（packageA，W3 实现完整表单） */
  goApplication() {
    if (this.data.house.rentStatus !== 'AVAILABLE') {
      wx.showToast({ title: '该房源当前不可申请', icon: 'none' })
      return
    }
    wx.navigateTo({ url: `/packageA-rental/pages/application/create?houseId=${this.houseId}` })
  },
})
