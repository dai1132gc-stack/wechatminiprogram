package com.rentalplatform.common.enums;

/** 合同状态：状态机迁移规则见技术方案 §10 与 state/ContractStateMachine。 */
public enum ContractStatus {
    DRAFT,
    PENDING_SIGN,
    EFFECTIVE,
    EXPIRED,
    TERMINATED,
    CANCELLED
}
