package com.rentalplatform.common.enums;

/**
 * 小程序双模式（技术方案 §11）：由「是否存在覆盖当前日期的生效合同」推导，
 * 不由角色硬编码。BOTH = 有租约但同时在找房。
 */
public enum UserMode {
    SEEKING,
    LIVING,
    BOTH
}
