Page({
  data: {
    // W1 骨架：预约列表数据在 W3 接 /appointments/mine
    items: [],
    loading: false,
  },

  onShow() {
    if (typeof this.getTabBar === 'function') {
      // SEEKING 模式下高亮 tab2；LIVING 模式该页不在菜单中，不高亮
      const mode = getApp().globalData.mode
      this.getTabBar().init(mode === 'LIVING' ? -1 : 1)
    }
  },
})
