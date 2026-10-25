package com.rentalplatform.config;

import com.rentalplatform.common.constant.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * W1 骨架版过滤器链：仅保证工程可启动、白名单可达。
 *
 * TODO(W2 认证落地)：
 *  1. JwtAuthenticationFilter：解析令牌 → 比对 auth:ver:{userId} → 构建 Authentication
 *  2. RestAuthenticationEntryPoint / RestAccessDeniedHandler：401/403 返回 JSON，禁止重定向
 *  3. RateLimitFilter：登录、刷新、上报限流
 *  4. authorizeHttpRequests 按 @PreAuthorize 收紧（当前 permitAll 仅限骨架阶段）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /** 白名单（供 W2 收紧时使用）：见 SecurityConstants.WHITELIST */
    public static String[] whitelist() {
        return SecurityConstants.WHITELIST;
    }
}
