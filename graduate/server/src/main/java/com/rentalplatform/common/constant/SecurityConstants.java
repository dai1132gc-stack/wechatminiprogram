package com.rentalplatform.common.constant;

/**
 * 安全相关常量。
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 白名单路径（技术方案 §5.5）：公开只读接口 + 登录/文档/设备旁路上报（HMAC 校验）。
     * 注意：/houses/**、/dict/** 下的写操作全部有 @PreAuthorize 兜底，URL 放行≠权限放行。
     */
    public static final String[] WHITELIST = {
            "/auth/login",
            "/auth/wx-login",
            "/auth/refresh",
            "/doc.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/ingest/**",
            "/ping",
            "/houses/**",
            "/dict/**"
    };

    /**
     * JWT 过滤器跳过清单：仅真正无需认证的端点。
     * /houses/**、/dict/** 是「公开读但可携带令牌」——过滤器必须运行（存在令牌则建立认证），
     * 不能与 WHITELIST 混用，否则带令牌访问公开接口时身份丢失。
     */
    public static final String[] SKIP_JWT = {
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
