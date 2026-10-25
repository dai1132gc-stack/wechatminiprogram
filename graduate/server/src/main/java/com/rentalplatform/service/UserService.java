package com.rentalplatform.service;

import com.rentalplatform.model.vo.UserInfo;

/** 当前用户相关。 */
public interface UserService {

    UserInfo getCurrentUser();

    /** 模式判定：存在覆盖当前日期的生效合同 → BOTH（可找房可居住），否则 SEEKING（§11） */
    CurrentMode getCurrentMode();

    record ContractBrief(Long id, String contractNo, Long houseId, String houseTitle,
                         String startDate, String endDate) {
    }

    record CurrentMode(String mode, java.util.List<ContractBrief> contracts) {
    }
}
