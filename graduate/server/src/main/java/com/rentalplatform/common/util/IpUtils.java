package com.rentalplatform.common.util;

import jakarta.servlet.http.HttpServletRequest;

/** 取客户端 IP：优先代理头（生产走 Nginx），本地开发直接 remoteAddr。 */
public final class IpUtils {

    private IpUtils() {
    }

    public static String clientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP"};
        for (String name : headers) {
            String value = request.getHeader(name);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
