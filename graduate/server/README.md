# server — 房屋租赁与室内环境监测系统后端

Spring Boot 3.3 + Java 21 + MyBatis-Plus + MySQL 8 + Redis 7（选型依据见技术方案 §2.1）。

## 当前状态（W2 认证已落地）

- ✅ `pom.xml`（web / security / validation / redis / mybatis-plus / jjwt / knife4j；websocket、amqp、oss、shedlock 注释待 W4-W6 启用）
- ✅ 统一响应契约层：`R` / `PageResult` / `ResultCode`（错误码唯一来源，§6.2 全量 41 个码）
- ✅ 全局异常处理 `GlobalExceptionHandler`（含 uk_house_active → 40003 映射）
- ✅ 13 个业务枚举，与《设计方案》§1.1、openapi.yaml 三方一致
- ✅ `application.yml`（context-path=/api/v1）+ dev/prod 分环境 + `.env.example`
- ✅ `PingController`：启动后 `GET /api/v1/ping` 验证存活与响应体格式
- ✅ W2：数据库 V1/V2/V3 已建（rental_env，9 表）；认证全链路落地：
  SecurityFilterChain + JwtAuthenticationFilter + TokenService（双令牌轮换 + 30 秒幂等宽限 +
  Redis 白名单/黑名单 + token_version 即时失效）+ 登录防爆破（5 次锁 15 分钟）
  已验证：登录/刷新/宽限重放/登出拉黑/版本失效/20005 锁定 全部通过
- ⏳ W3+：controller / service / mapper / state / guard 按 openapi.yaml 逐模块实现

## 契约唯一来源

**`docs/openapi.yaml`**（58 个接口、57 个 schema）。Knife4j UI（`/doc.html`）由代码注解生成，
与 yaml 冲突时以 yaml 为准。

## 启动（W2 起需要依赖）

```bash
# 1. 配置环境变量（对照 .env.example）
# 2. 启动（本机暂未装 Maven，先安装 Maven 3.9 或使用 IDE 运行；后续补 mvnw）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# 3. 验证
curl http://localhost:8080/api/v1/ping
# → {"code":0,"message":"ok","data":"pong","traceId":null,"timestamp":...}
```

## 分层职责边界（写代码前重读一遍）

| 层 | 可以做 | 禁止做 |
|---|---|---|
| Controller | 参数校验、调用 Service、包装 R 返回、权限注解 | 写业务判断、直接调 Mapper |
| Service | 业务编排、事务、状态机调用、权限守卫调用 | 处理 HttpServletRequest、返回 Entity |
| Mapper | 数据访问 | 写业务逻辑 |
| State | 状态迁移规则表与校验 | 访问数据库 |
