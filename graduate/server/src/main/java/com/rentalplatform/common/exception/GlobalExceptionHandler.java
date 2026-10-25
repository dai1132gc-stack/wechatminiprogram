package com.rentalplatform.common.exception;

import com.rentalplatform.common.result.R;
import com.rentalplatform.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理（技术方案 §6.3）。
 * 关键原则：Exception 兜底分支绝不把 e.getMessage() 返回给前端——
 * SQL 异常可能带表名字段名，前端只拿 traceId，运维凭 traceId 查日志。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常 code={} message={}", e.getResultCode().getCode(), e.getMessage());
        return R.fail(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValid(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String message = fe == null
                ? ResultCode.PARAM_INVALID.getMessage()
                : ResultCode.PARAM_INVALID.getMessage() + "：" + fe.getField() + " " + fe.getDefaultMessage();
        log.warn("参数校验失败 {}", message);
        return R.fail(ResultCode.PARAM_INVALID, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleUnreadable(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return R.fail(ResultCode.PARAM_INVALID, "请求体格式错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return R.fail(ResultCode.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public R<Void> handleNotFound(NoResourceFoundException e) {
        return R.fail(ResultCode.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleUploadSize(MaxUploadSizeExceededException e) {
        return R.fail(ResultCode.FILE_TOO_LARGE);
    }

    /**
     * 并发双签兜底：uk_house_active 唯一索引触发 DuplicateKeyException，
     * 按约束名映射业务码（技术方案 §4.4）。
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R<Void> handleDuplicateKey(DuplicateKeyException e) {
        String msg = e.getMostSpecificCause().getMessage();
        if (msg != null && msg.contains("uk_house_active")) {
            return R.fail(ResultCode.HOUSE_ALREADY_RENTED);
        }
        log.warn("唯一键冲突: {}", msg);
        return R.fail(ResultCode.DB_ERROR, "数据已存在或正在被他人操作，请刷新后重试");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleAccessDenied(AccessDeniedException e) {
        return R.fail(ResultCode.NO_PERMISSION);
    }

    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuthentication(AuthenticationException e) {
        return R.fail(ResultCode.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        // error 级 + 完整堆栈；traceId 已在 R 中带给前端
        log.error("系统内部错误", e);
        return R.fail(ResultCode.SYSTEM_ERROR);
    }
}
