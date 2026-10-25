package com.rentalplatform.model.vo;

import lombok.Builder;
import lombok.Data;

/** POST /auth/login、/auth/wx-login 出参（openapi.yaml LoginData / WxLoginData） */
@Data
@Builder
public class LoginVO {

    private String accessToken;
    private String refreshToken;
    /** access token 有效期（秒） */
    private long expiresIn;
    private UserInfo userInfo;
    /** Web 端按钮级权限码；ADMIN 为 ["*"] */
    private String[] permissions;
    /** 小程序端模式；Web 端不返回该字段 */
    private String mode;
}
