package com.rentalplatform.common.enums;

/** 设备状态：由 DeviceOfflineJob 每 5 分钟依据 Redis 在线键扫描刷新。 */
public enum DeviceStatus {
    ONLINE,
    OFFLINE,
    UNBOUND,
    FAULT
}
