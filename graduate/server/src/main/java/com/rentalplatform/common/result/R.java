package com.rentalplatform.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.slf4j.MDC;

/**
 * 统一响应体（技术方案 §6.1）：
 * code 业务码（前端只认这个）、message 面向用户、data 无数据为 null、
 * traceId 全链路追踪、timestamp 服务端毫秒时间戳。
 * data 为 null 时仍输出 "data": null，不省略字段。
 */
@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class R<T> {

    private final int code;
    private final String message;
    private final T data;
    private final String traceId;
    private final long timestamp;

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = MDC.get("traceId");
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(resultCode.getCode(), message, null);
    }
}
