package com.rentalplatform.common.exception;

import com.rentalplatform.common.result.ResultCode;
import lombok.Getter;

/**
 * 可预期业务异常，携带 ResultCode。GlobalExceptionHandler 统一捕获，
 * Controller 中不写 try-catch（技术方案 §6.5）。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /** message 要具体到用户能看懂，如「该房源已被其他租客签约，请选择其他房源」。 */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
