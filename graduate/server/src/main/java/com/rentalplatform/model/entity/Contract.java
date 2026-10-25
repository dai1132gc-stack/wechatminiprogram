package com.rentalplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 表 contract。W2 仅用于模式判定查询，完整状态机在 W4 落地。 */
@Data
@TableName("contract")
public class Contract {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String contractNo;
    private Long houseId;
    private Long tenantId;
    private Long landlordId;
    private LocalDate startDate;
    private LocalDate endDate;
    private java.math.BigDecimal rent;
    private java.math.BigDecimal deposit;
    private String status;
    /** 生效时=1，其余=NULL；uk_house_active 唯一索引兜底并发双签 */
    private Integer activeFlag;
    private LocalDateTime signTime;
    private LocalDateTime tenantSignTime;
    private LocalDateTime landlordSignTime;
    private LocalDateTime terminateTime;
    private String terminateReason;
    private String pdfUrl;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
