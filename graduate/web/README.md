# web — Web 管理后台（Vue 3 + TypeScript + Element Plus）

技术栈对齐技术方案 §2.2：Vite 5 / Vue 3.4 / TS 5.5 / Element Plus 2.8 / Pinia 2 / Vue Router 4 / Axios / SCSS。

## 启动

```bash
npm install
npm run dev        # http://localhost:5173
npm run build      # vue-tsc 类型检查 + 生产构建
```

## Mock 登录（W1 演示账号）

| 账号 | 密码 | 角色 | 说明 |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | 全部菜单，工作台统计卡片 |
| `landlord1` | `123456` | LANDLORD | 无「房源审核 / 环境总览 / 系统」菜单（角色过滤验证） |

登录成功 → 存双令牌 → 守卫拉 `/users/me` → 按角色生成动态路由 → 工作台。
连续输错 5 次密码 → 模拟 `20005` 锁定（按钮禁用倒计时）。

## Mock 说明（切真接口零改动）

- `mock/` 目录由 `vite-plugin-mock` 挂载在 dev server 上，响应体与技术方案 §6.1 逐字段一致（code/message/data/traceId/timestamp）。
- 切后端：`vite.config.ts` 注释掉 `viteMockServe`、放开 `server.proxy`，前端代码零改动。
- 异常注入：任意列表接口加 `?mock=error` 返回 90001，用于验证错误态。
- `mock/_mockdb.ts` 是确定性伪随机数据（每次刷新一致，避免演示闪烁）。

## 目录职责

```
src/
├── api/          # 一个模块一个文件，只放请求函数（auth/user/house/stat）
├── router/       # routes.ts 静态+动态路由表；guard.ts 登录态/角色守卫
├── store/        # Pinia：user(令牌/角色) permission(菜单过滤) app(侧边栏)
├── layout/       # 全局布局：Sidebar(角色菜单) Navbar(铃铛轮询/用户菜单) Breadcrumb
├── views/        # W1 实现：login/dashboard/404/house-list；其余为占位页（W2-W6 逐周替换）
├── utils/        # request.ts(Axios+401刷新队列) auth.ts(令牌读写)
├── types/        # api.d.ts 与 openapi.yaml 对齐的类型
└── styles/       # SCSS + Element 变量覆盖（品牌蓝 #185FA5）
```

## 落地进度（对照设计方案 §5）

- ✅ W1：项目初始化、布局、路由、登录（Mock）
- ⏳ W2：房源列表 + 编辑 + 详情（PageTable/SearchForm/StatusTag 组件抽取）
- ⏳ W3：房源审核 + 租赁申请
- ⏳ W4：合同列表/详情 + 设备管理
- ⏳ W5：环境总览 + 环境详情（EnvChart）+ 告警处置
- ⏳ W6：用户管理 + 操作日志 + 字典 + 打磨（ESLint/husky/commitlint）
