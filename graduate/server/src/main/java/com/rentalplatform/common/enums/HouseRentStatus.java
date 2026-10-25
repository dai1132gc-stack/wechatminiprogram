package com.rentalplatform.common.enums;

/**
 * 房源租赁状态：《设计方案》§1.1 —— 租赁状态管「能不能被租」。
 * 与审核状态分开存是刻意设计（两链各 3~4 态，避免 3×4 组合爆炸，技术方案 §4.3）。
 */
public enum HouseRentStatus {
    AVAILABLE,
    LOCKED,
    RENTED,
    OFFLINE
}
