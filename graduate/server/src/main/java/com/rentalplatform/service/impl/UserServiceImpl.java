package com.rentalplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rentalplatform.mapper.ContractMapper;
import com.rentalplatform.mapper.UserMapper;
import com.rentalplatform.model.entity.Contract;
import com.rentalplatform.model.entity.SysUser;
import com.rentalplatform.model.vo.UserInfo;
import com.rentalplatform.security.SecurityUtils;
import com.rentalplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ContractMapper contractMapper;

    @Override
    public UserInfo getCurrentUser() {
        SysUser user = userMapper.selectById(SecurityUtils.getUserId());
        if (user == null) {
            throw new com.rentalplatform.common.exception.AuthException(
                    com.rentalplatform.common.result.ResultCode.UNAUTHORIZED);
        }
        return UserInfo.from(user);
    }

    @Override
    public CurrentMode getCurrentMode() {
        return getModeByUserId(SecurityUtils.getUserId());
    }

    @Override
    public CurrentMode getModeByUserId(long userId) {
        LocalDate today = LocalDate.now();
        List<Contract> contracts = contractMapper.selectList(
                new LambdaQueryWrapper<Contract>()
                        .eq(Contract::getTenantId, userId)
                        .eq(Contract::getActiveFlag, 1)
                        .le(Contract::getStartDate, today)
                        .ge(Contract::getEndDate, today));
        List<ContractBrief> briefs = contracts.stream()
                .map(c -> new ContractBrief(c.getId(), c.getContractNo(), c.getHouseId(),
                        null, c.getStartDate().toString(), c.getEndDate().toString()))
                .toList();
        return new CurrentMode(contracts.isEmpty() ? "SEEKING" : "BOTH", briefs);
    }
}
