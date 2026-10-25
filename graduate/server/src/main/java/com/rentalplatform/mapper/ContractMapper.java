package com.rentalplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rentalplatform.model.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
}
