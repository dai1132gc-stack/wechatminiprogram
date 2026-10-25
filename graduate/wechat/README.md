# wechat — 微信小程序（原生 + TDesign + ec-canvas）

技术栈对齐技术方案 §2.3：原生 WXML/WXSS/JS、TDesign Miniprogram（W2 起使用组件）、
echarts-for-weixin（W5 趋势图）、自研轻量 observable。

## 启动

1. 微信开发者工具 → 导入本目录 → AppID 用测试号（已填 `touristappid`，可换成自己的）
2. 详情 → 本地设置 → 勾选「不校验合法域名」（仅开发期）
3. W1 直接可跑：当前 `config/env.js` 中 `useMock: true`，无需后端
4. W2 起使用 TDesign 组件：目录下 `npm install` → 工具 → 构建 npm

## W1 已实现（Mock 登录全流程）

- 启动流程：`app.js onLaunch` 读 token → 静默校验 `/users/me` → 拉 `/users/me/mode` → 进首页；失败停登录页
- 登录页：协议勾选 → `wx.login` → `POST /auth/wx-login`（Mock 轮换返回找房/居住两种用户，演示双模式）
- 双模式 tabBar（custom-tab-bar）：SEEKING 找房/预约/我的；LIVING 我家/趋势/告警/我的
- 找房闭环（Mock 数据）：首页房源流 → 列表搜索 → 详情（含甲醛超标红色分支）→ 预约/申请入口
- 「我家」看板：占位读数卡片（W5 接真实环境链路）
- 请求层：`utils/request.js` Promise 化 + 令牌注入 + 20002 静默刷新重放 + 20001/20003 reLaunch 登录页

## ⚠️ 与《设计方案》§3.1 tabBar 的差异（微信平台硬约束）

| 约束 | 处理 |
|---|---|
| tabBar.list 页面必须在主包 | 「预约」`pages/appointment/list` 与「我家」`pages/dashboard/index` 从分包提升为主包页面 |
| 两模式 tab 并集 6 个 > 微信上限 5 | LIVING 的「趋势」「告警」保留在 packageB（ec-canvas 体积必须分包），通过 `wx.reLaunch` 跳转，页内提供「返回我家」入口 |

后续若要趋势/告警也显示 tabBar，需把这两个页面做轻量壳提升主包、图表仍留分包加载——W5 环境模块开工时定稿。

## 目录职责

```
config/          env.js 三环境 + useMock 总开关（上线前置 false）
utils/           request(wx.request 封装+401刷新) auth(登录态) storage(带过期) format(数值/时间/枚举文案)
api/             一个模块一个文件（auth/house）
store/           轻量 observable（user：用户/模式）
mock/            Mock 分发器 + handlers + data（响应体与 openapi.yaml 一致）
custom-tab-bar/  按模式渲染菜单
pages/           主包：index(双模式首页) login house/list house/detail mine appointment/list dashboard
packageA-rental/ 分包A：预约/申请/合同（W3-W4 实现）
packageB-env/    分包B：趋势/告警/设备（W5 实现，含 ec-canvas）
```

## 落地进度（对照设计方案 §5）

- ✅ W1：初始化、tabBar、登录（Mock）
- ⏳ W2：首页找房 + 房源列表 + 详情（TDesign 组件接入）
- ⏳ W3：预约 + 申请
- ⏳ W4：合同列表/详情
- ⏳ W5：环境看板 + 趋势（ec-canvas）+ 告警
- ⏳ W6：我的 + 设备信息 + 打磨
