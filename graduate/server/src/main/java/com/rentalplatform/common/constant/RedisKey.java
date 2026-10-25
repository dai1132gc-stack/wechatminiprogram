package com.rentalplatform.common.constant;

/**
 * Redis key 前缀集中定义（技术方案 §2.4）：全部设 TTL，禁止无过期键。
 */
public final class RedisKey {

    private RedisKey() {
    }

    /** refresh 白名单：auth:refresh:{userId}:{jti} → {"deviceId","expireAt"}，TTL 7d */
    public static final String REFRESH = "auth:refresh:";
    /** 已使用的 refresh jti（30 秒幂等宽限，防并发刷新误判重放） */
    public static final String REFRESH_USED = "auth:refresh:used:";
    /** access 黑名单：auth:blacklist:{jti}，TTL = 剩余有效期 */
    public static final String BLACKLIST = "auth:blacklist:";
    /** 令牌版本：auth:ver:{userId}，改密/改角色/禁用时 +1 */
    public static final String TOKEN_VERSION = "auth:ver:";
    /** 登录失败计数：auth:fail:{username}，5 次锁 15 分钟 */
    public static final String LOGIN_FAIL = "auth:fail:";
    /** 用户 → 可见房源集合缓存（权限缓存，租约变更时失效） */
    public static final String HOUSE_VISIBLE = "perm:house:visible:";
    /** 设备在线状态：device:online:{deviceSn} */
    public static final String DEVICE_ONLINE = "device:online:";
    /** 接口限流计数 */
    public static final String RATE_LIMIT = "ratelimit:";
}
