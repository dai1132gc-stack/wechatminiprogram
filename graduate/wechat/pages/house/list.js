const api = require('../../api/house')
const format = require('../../utils/format')

Page({
  data: {
    keyword: '',
    houses: [],
    loading: false,
    finished: false,
    pageNum: 1,
    pageSize: 10,
  },

  onLoad() {
    this.fetchList(true)
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.fetchList(true)
  },

  async fetchList(reset = false) {
    if (this.data.loading) return
    if (reset) this.setData({ pageNum: 1, finished: false, houses: [] })
    if (this.data.finished) return
    this.setData({ loading: true })
    try {
      const { pageNum, pageSize, keyword } = this.data
      const envelope = await api.listHouses({ pageNum, pageSize, keyword })
      if (envelope.code === 0 && envelope.data) {
        const mapped = envelope.data.list.map((h) => ({
          ...h,
          rentText: format.rent(h.rent),
          areaText: format.area(h.area),
          rentStatusText: format.statusText('rentStatus', h.rentStatus),
        }))
        const houses = reset ? mapped : this.data.houses.concat(mapped)
        this.setData({
          houses,
          finished: houses.length >= envelope.data.total,
          pageNum: pageNum + 1,
        })
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  onReachBottom() {
    this.fetchList()
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/house/detail?id=${id}` })
  },
})
