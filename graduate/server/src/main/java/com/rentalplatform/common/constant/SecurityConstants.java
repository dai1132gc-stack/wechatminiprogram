package com.rentalplatform.common.constant;

/**
 * 安全相关常量。
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 白名单路径（技术方案 §5.5）：登录/刷新/文档/设备旁路上报（HMAC 校验） */
    public static final String[] WHITELIST = {
            "/auth/login",
            "/auth/wx-login",
            "/auth/refresh",
            "/doc.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/ingest/**",
            "/ping"
    };

    /** 请求头：traceId 透传，便于前后端日志对照 */
    public static final String TRACE_HEADER = "X-Trace-Id";
}
