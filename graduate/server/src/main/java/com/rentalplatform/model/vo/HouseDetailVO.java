package com.rentalplatform.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 房源详情（openapi.yaml HouseDetail）。房东联系方式仅本人/ADMIN 可见。 */
@Data
@Builder
public class HouseDetailVO {

    private Long id;
    private String title;
    private String district;
    private String street;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String layout;
    private BigDecimal area;
    private Integer floor;
    private Integer totalFloor;
    private String orientation;
    private String decoration;
    private BigDecimal rent;
    private BigDecimal deposit;
    private String coverUrl;
    private List<String> imageUrls;
    private String description;
    private List<String> facilities;
    private String auditStatus;
    private String rentStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
    private LocalDateTime createdAt;
    private Integer version;

    /** 仅本人/ADMIN 非空 */
    private LandlordContact landlord;

    /** 未绑定设备为 null */
    private DeviceInfo device;

    /** 环境列（与列表一致） */
    private String deviceStatus;
    private BigDecimal latestHcho;
    private Boolean hchoExceeded;

    public record LandlordContact(Long id, String nickname, String phone) {
    }

    public record DeviceInfo(Long id, String name, String status, String sensorProfile) {
    }
}
