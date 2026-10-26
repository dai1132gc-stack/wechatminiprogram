# 毕业设计 · 房屋租赁与室内环境监测系统

单仓多项目（对应技术方案 §3 monorepo，本机目录映射）：

| 目录 | 子项目 | 技术栈 | W1 状态 |
|---|---|---|---|
| `server/` | 后端服务 | Spring Boot 3.3 + Java 21 + MyBatis-Plus | 骨架 + 契约层代码，`docs/openapi.yaml` 为**契约唯一来源** |
| `web/` | Web 管理后台 | Vue 3 + TS + Element Plus + Vite | Mock 登录跑通（admin/admin123、landlord1/123456） |
| `wechat/` | 微信小程序 | 原生 + TDesign | Mock 微信登录跑通（双模式 tabBar） |

设计文档在 `C:\Users\HONOR\Desktop\毕业设计\初步项目\docs\`（技术方案 + 前端设计方案）。

## 快速启动（W2：Web 登录已走真实后端）

```bash
# 0) 依赖：MySQL(3306, root/123456) + Redis(6379，C:/Users/HONOR/dev-tools/redis，需先启动)
# 1) 后端（首次构建用 C:/Users/HONOR/dev-tools/apache-maven-3.9.16/bin/mvn）
cd server && mvn spring-boot:run -Dspring-boot.run.profiles=dev   # → :8080/api/v1
# 2) Web 管理后台（auth + 房源模块走真实后端，仅 stat 仍 Mock 待 W6）
cd web && npm install && npm run dev    # → http://127.0.0.1:5180  admin/admin123 或 landlord1/123456
# 3) 小程序：微信开发者工具导入 wechat/（AppID 已配测试号 wx0ebf901e27616902），
#    本地设置勾「不校验合法域名」；W3 起默认连真实后端（useMock:false，离线演示改回 true）

# 小程序：微信开发者工具导入 wechat/ 目录（AppID 已填 touristappid 测试号）
# config/env.js 中 useMock=true，无需后端
```

## 开发顺序（对照设计方案 §5 落地建议）

- ✅ W1：OpenAPI 契约冻结 + 两端骨架 + Mock 登录
- ⏳ W2：server 认证体系落地（SecurityConfig 内 TODO 清单）+ Web 房源三页 + 小程序找房
- ⏳ W3：房源审核 + 租赁申请；W4：合同 + 设备；W5：环境监测链路；W6：系统管理 + 打磨

## 契约对齐铁律

1. 接口路径、字段、枚举、错误码**只认 `server/docs/openapi.yaml`**；
2. Mock 响应体与真实后端逐字段一致（两端 Mock 均按 §6.1 信封实现）；
3. 切真接口：Web 注释 `viteMockServe` 放开 proxy；小程序 `useMock: false` —— 前端业务代码零改动。

## 已知事项

- Web dev server 用 5180：本机 5173 被 HnAgentStudio（HnDocManagerJSServe）占用；
- 本机未装 Maven（技术方案 §17）：server 首次构建前需装 Maven 3.9 或用 IDE 运行；
- 小程序 tabBar 与设计方案 §3.1 的差异（微信平台硬约束）见 `wechat/README.md`；
- vite-plugin-mock 已知怪癖：修改 `mock/_mockdb.ts` 这类被引用的公共文件后，热重载可能不彻底，重启 dev server 即可。
