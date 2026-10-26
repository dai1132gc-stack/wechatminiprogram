package com.rentalplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rentalplatform.model.entity.EnvRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnvRecordMapper extends BaseMapper<EnvRecord> {

    /** 每个房源最新一条环境记录（列表「环境列」数据源，SQL 见 mapper/EnvRecordMapper.xml） */
    List<EnvRecord> selectLatestByHouseIds(@org.apache.ibatis.annotations.Param("houseIds") List<Long> houseIds);
}
