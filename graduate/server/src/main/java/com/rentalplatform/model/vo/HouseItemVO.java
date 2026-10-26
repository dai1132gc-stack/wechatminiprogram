package com.rentalplatform.model.vo;

import com.rentalplatform.model.entity.House;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 房源列表项（openapi.yaml HouseItem）：环境列由后端一次 join 返回，前端禁止循环请求。 */
@Data
@Builder
public class HouseItemVO {

    private Long id;
    private String title;
    private String district;
    private String address;
    private String layout;
    private BigDecimal area;
    private BigDecimal rent;
    private BigDecimal deposit;
    private String coverUrl;
    private String auditStatus;
    private String rentStatus;
    private String deviceStatus;
    /** 最新甲醛读数（mg/m³，估算）；无设备为 null */
    private BigDecimal latestHcho;
    private Boolean hchoExceeded;
    private LocalDateTime createdAt;

    /** 甲醛判定阈值（mg/m³），服务端下发口径，前端不硬编码 */
    public static final BigDecimal HCHO_THRESHOLD = new BigDecimal("0.08");

    public static HouseItemVO baseOf(House h) {
        return HouseItemVO.builder()
                .id(h.getId())
                .title(h.getTitle())
                .district(h.getDistrict())
                .address(h.getAddress())
                .layout(h.getLayout())
                .area(h.getArea())
                .rent(h.getRent())
                .deposit(h.getDeposit())
                .coverUrl(h.getCoverUrl())
                .auditStatus(h.getAuditStatus())
                .rentStatus(h.getRentStatus())
                .createdAt(h.getCreatedAt())
                .build();
    }

    public void fillEnv(String deviceStatus, BigDecimal latestHcho) {
        this.deviceStatus = deviceStatus;
        this.latestHcho = latestHcho;
        this.hchoExceeded = latestHcho != null && latestHcho.compareTo(HCHO_THRESHOLD) > 0;
    }
}
