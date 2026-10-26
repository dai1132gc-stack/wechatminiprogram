package com.rentalplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 表 device（W3 用于房源列表环境列 join 与详情设备信息）。 */
@Data
@TableName("device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceSn;
    private Long houseId;
    private String name;
    private String installPosition;
    /** ONLINE / OFFLINE / UNBOUND / FAULT */
    private String status;
    private String sensorProfile;
    private LocalDateTime lastOnlineAt;
    private String firmwareVersion;
    private String secretKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
