/**
 * 自定义 tabBar：按模式渲染（设计方案 §3.1）。
 *
 * 微信平台约束（与文档 §3.1 的差异，见项目 README）：
 * 1. tabBar.list 页面必须在主包 —— 因此「预约」「我家(dashboard)」提升为主包页面；
 * 2. 两模式 tab 并集为 6 个 > 微信上限 5 —— 因此 LIVING 的「趋势/告警」留在 packageB，
 *    通过 wx.reLaunch 跳转（tabBar 页面才能 switchTab；reLaunch 后这些页面无 tabBar，
 *    页面内提供返回入口）。
 */
const SEEKING_TABS = [
  { path: '/pages/index/index', text: '找房', icon: '🔍', kind: 'tab' },
  { path: '/pages/appointment/list', text: '预约', icon: '📅', kind: 'tab' },
  { path: '/pages/mine/index', text: '我的', icon: '👤', kind: 'tab' },
]

const LIVING_TABS = [
  { path: '/pages/dashboard/index', text: '我家', icon: '🏠', kind: 'tab' },
  { path: '/packageB-env/pages/history/index', text: '趋势', icon: '📈', kind: 'launch' },
  { path: '/packageB-env/pages/alarm/list', text: '告警', icon: '🔔', kind: 'launch' },
  { path: '/pages/mine/index', text: '我的', icon: '👤', kind: 'tab' },
]

Component({
  data: {
    selected: 0,
    mode: 'SEEKING',
    list: SEEKING_TABS,
  },

  methods: {
    /** 页面 onShow 调用：刷新模式菜单与高亮 */
    init(selected = 0) {
      const app = getApp()
      const mode = (app && app.globalData.mode) || 'SEEKING'
      const list = mode === 'LIVING' || mode === 'BOTH' ? LIVING_TABS : SEEKING_TABS
      this.setData({ mode, list, selected })
    },

    setSelected(selected) {
      this.setData({ selected })
    },

    onTap(e) {
      const { index } = e.currentTarget.dataset
      const item = this.data.list[index]
      if (!item) return
      if (item.kind === 'tab') {
        wx.switchTab({ url: item.path })
      } else {
        // 分包页不能 switchTab；reLaunch 清栈避免页面层级堆积
        wx.reLaunch({ url: item.path })
      }
    },
  },
})
