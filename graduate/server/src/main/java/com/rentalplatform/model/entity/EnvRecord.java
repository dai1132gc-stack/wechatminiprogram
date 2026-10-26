package com.rentalplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 表 env_record（W3 仅读最新值用于列表环境列；上报链路 W5 落地）。 */
@Data
@TableName("env_record")
public class EnvRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;
    private String deviceSn;
    private Long houseId;
    private Long contractId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal hcho;
    private BigDecimal tvoc;
    private Integer co2;
    private Integer pm25;
    private Integer qualityFlag;
    private String sensorProfile;
    private Integer sampleCount;
    private LocalDateTime collectTime;
    private LocalDateTime receiveTime;
}
