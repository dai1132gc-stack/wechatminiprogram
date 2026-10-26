package com.rentalplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 表 house（V1__schema.sql）。审核状态与租赁状态分开存是刻意设计（§4.3）。 */
@Data
@TableName("house")
public class House {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long landlordId;
    private Long orgId;
    private String title;
    private String district;
    private String street;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private BigDecimal area;
    private String layout;
    private Integer floor;
    private Integer totalFloor;
    private String orientation;
    private String decoration;
    private BigDecimal rent;
    private BigDecimal deposit;
    /** 逗号分隔设施编码 */
    private String facilities;
    private String coverUrl;
    private String description;
    /** PENDING / APPROVED / REJECTED */
    private String auditStatus;
    /** AVAILABLE / LOCKED / RENTED / OFFLINE */
    private String rentStatus;
    private String auditRemark;
    private Long auditorId;
    private LocalDateTime auditTime;

    @Version
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
