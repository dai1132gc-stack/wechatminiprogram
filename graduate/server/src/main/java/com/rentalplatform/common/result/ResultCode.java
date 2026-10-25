package com.rentalplatform.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务码唯一来源（技术方案 §6.2）。五位数字按域分段，禁止散落魔法数字。
 * 前端按《设计方案》§1.4 的 错误码 → UI 行为映射 分派。
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "ok"),

    /* 10xxx 通用 */
    PARAM_INVALID(10001, "参数校验失败"),
    METHOD_NOT_SUPPORTED(10002, "请求方法不支持"),
    NOT_FOUND(10003, "资源不存在"),
    TOO_FREQUENT(10004, "请求过于频繁，请稍后重试"),
    FILE_TYPE_NOT_ALLOWED(10005, "文件类型不允许"),
    FILE_TOO_LARGE(10006, "文件超出大小限制"),

    /* 20xxx 认证 */
    UNAUTHORIZED(20001, "未登录或令牌缺失"),
    TOKEN_EXPIRED(20002, "访问令牌已过期"),
    REFRESH_INVALID(20003, "刷新令牌无效，请重新登录"),
    ACCOUNT_DISABLED(20004, "账号已被禁用"),
    LOGIN_TOO_FREQUENT(20005, "登录尝试过于频繁，请 15 分钟后再试"),
    USERNAME_PASSWORD_ERROR(20006, "用户名或密码错误"),
    WX_CODE_INVALID(20007, "微信登录凭证无效"),

    /* 30xxx 授权 */
    NO_PERMISSION(30001, "无操作权限"),
    NO_DATA_PERMISSION(30002, "无权访问该数据"),
    LEASE_EXPIRED(30003, "租约已失效，环境数据不可见"),
    HOUSE_NOT_IN_SCOPE(30004, "该房源不在你的管理范围"),

    /* 40xxx 租赁业务 */
    HOUSE_STATUS_INVALID(40001, "房源当前状态不允许该操作"),
    APPLICATION_DUPLICATED(40002, "你已提交过申请，请勿重复提交"),
    HOUSE_ALREADY_RENTED(40003, "该房源已被其他租客签约，请选择其他房源"),
    CONTRACT_TRANSITION_INVALID(40004, "合同状态迁移非法"),
    CONTRACT_DATE_CONFLICT(40005, "租期与现有合同冲突"),
    HOUSE_NOT_APPROVED(40006, "房源尚未通过审核"),
    CONTRACT_EFFECTIVE_IMMUTABLE(40007, "合同已生效，无法修改"),

    /* 50xxx 设备与环境 */
    DEVICE_NOT_FOUND(50001, "设备不存在"),
    DEVICE_ALREADY_BOUND(50002, "设备已绑定其他房源"),
    DEVICE_OFFLINE(50003, "设备当前离线"),
    INGEST_TIMESTAMP_INVALID(50004, "上报数据时间戳异常"),
    DEVICE_SIGNATURE_INVALID(50005, "设备签名校验失败"),
    ENV_DATA_NOT_VISIBLE(50006, "环境数据不在可见范围内"),

    /* 90xxx 系统 */
    SYSTEM_ERROR(90001, "系统内部错误，请联系管理员"),
    THIRD_PARTY_UNAVAILABLE(90002, "第三方服务暂不可用"),
    DB_ERROR(90003, "数据库操作失败"),
    FILE_STORAGE_ERROR(90004, "文件存储服务异常");

    private final int code;
    private final String message;
}
