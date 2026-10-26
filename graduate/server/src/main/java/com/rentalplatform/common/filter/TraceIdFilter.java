package com.rentalplatform.common.filter;

import com.rentalplatform.common.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * traceId 全链路串联（技术方案 §7.2）：优先透传上游（Nginx）的 X-Trace-Id，
 * 没有则生成 12 位十六进制；写 MDC（日志 pattern 输出）+ 响应头回传前端。
 * R 构造时从 MDC 取值，因此本过滤器必须先于任何业务代码执行。
 * finally 清理，防止线程池复用导致串号。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String MDC_KEY = "traceId";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String traceId = request.getHeader(SecurityConstants.TRACE_HEADER);
            if (!StringUtils.hasText(traceId)) {
                traceId = generate();
            }
            MDC.put(MDC_KEY, traceId);
            response.setHeader(SecurityConstants.TRACE_HEADER, traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private String generate() {
        return String.format("%012x", RANDOM.nextLong() & 0xFFFFFFFFFFFFL);
    }
}
