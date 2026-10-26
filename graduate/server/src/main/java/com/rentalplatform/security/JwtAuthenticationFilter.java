package com.rentalplatform.security;

import com.rentalplatform.common.result.R;
import com.rentalplatform.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.rentalplatform.common.constant.SecurityConstants.TOKEN_PREFIX;
import static com.rentalplatform.common.constant.SecurityConstants.SKIP_JWT;

/**
 * 解析 access token → 校验黑名单与 token_version → 构建 Authentication。
 * - 过期：HTTP 401 + code 20002（前端据此静默刷新后重放）
 * - 缺失/伪造/版本不符/已拉黑：放行为未认证，由 RestAuthenticationEntryPoint 统一 401 + 20001
 * token_version 比对直接用 DB 实查值（LoginUser 构造时已带出），
 * 改密/改角色/禁用后旧令牌立即失效，无需等自然过期（技术方案 §5.1）。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider provider;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String pattern : SKIP_JWT) {
            if (new AntPathRequestMatcher(pattern).matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.substring(TOKEN_PREFIX.length());
        try {
            var claims = provider.parse(token);
            if (!JwtTokenProvider.TYPE_ACCESS.equals(claims.type())) {
                chain.doFilter(request, response);
                return;
            }
            if (tokenService.isBlacklisted(claims.jti())) {
                chain.doFilter(request, response);
                return;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername("id:" + claims.userId());
            if (userDetails instanceof LoginUser lu
                    && (lu.getTokenVersion() == null || lu.getTokenVersion() != claims.tokenVersion())) {
                chain.doFilter(request, response);
                return;
            }
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            writeEnvelope(response, ResultCode.TOKEN_EXPIRED);
            return;
        } catch (JwtException | NumberFormatException e) {
            // 伪造/损坏令牌：按未认证处理，交给 EntryPoint
        }
        chain.doFilter(request, response);
    }

    private void writeEnvelope(HttpServletResponse response, ResultCode code) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(code)));
    }
}
