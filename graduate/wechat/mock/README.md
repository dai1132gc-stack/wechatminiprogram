# 小程序 Mock 说明

- 开关：`config/env.js` 中 `useMock: true`（上线前置 false）。
- 原则：Mock 与真实请求走同一 `utils/request.js` 出口，切换只改一个布尔值（技术方案 §0.3）。
- 响应体与 `server/docs/openapi.yaml`、Web 端 `web/mock/` 同一套信封：`{code, message, data, traceId, timestamp}`。
- 登录演示：`/auth/wx-login` 轮换返回 SEEKING（找房模式）与 LIVING（居住模式）用户，便于演示双模式 tabBar。
- 图片用本地资源或空值，不依赖外网 URL。
