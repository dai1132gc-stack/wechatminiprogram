package com.rentalplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rentalplatform.model.entity.House;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseMapper extends BaseMapper<House> {
}
