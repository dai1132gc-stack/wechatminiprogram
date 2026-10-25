package com.rentalplatform.common.exception;

import com.rentalplatform.common.result.ResultCode;

/** 授权类异常（30xxx）：无操作权限 / 无数据权限 / 租约失效。 */
public class PermissionException extends BusinessException {

    public PermissionException(ResultCode resultCode) {
        super(resultCode);
    }

    public PermissionException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }
}
