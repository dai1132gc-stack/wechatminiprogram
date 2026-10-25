package com.rentalplatform.common.exception;

import com.rentalplatform.common.result.ResultCode;

/** 认证类异常（20xxx）：未登录 / 令牌过期 / 刷新令牌无效等。 */
public class AuthException extends BusinessException {

    public AuthException(ResultCode resultCode) {
        super(resultCode);
    }

    public AuthException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }
}
