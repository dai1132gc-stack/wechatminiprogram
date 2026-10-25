package com.rentalplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** application.yml 中 rental.* 配置（JWT 与微信） */
@Data
@Configuration
@ConfigurationProperties(prefix = "rental")
public class RentalProperties {

    private Jwt jwt = new Jwt();
    private Wechat wechat = new Wechat();

    @Data
    public static class Jwt {
        private String secret;
        /** access token 有效期（分钟） */
        private long accessTtlMinutes = 120;
        /** refresh token 有效期（天） */
        private long refreshTtlDays = 7;
    }

    @Data
    public static class Wechat {
        private String appid;
        private String secret;
    }
}
