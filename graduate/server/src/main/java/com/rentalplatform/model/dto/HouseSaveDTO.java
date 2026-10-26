package com.rentalplatform.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/** 发布/编辑房源入参（openapi.yaml HouseSaveDTO）。 */
@Data
public class HouseSaveDTO {

    @NotBlank @Size(min = 2, max = 100)
    private String title;

    @NotBlank
    private String district;

    private String street;

    @NotBlank @Size(max = 200)
    private String address;

    private BigDecimal lat;
    private BigDecimal lng;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal area;

    @NotBlank
    private String layout;

    @Min(1) private Integer floor;
    @Min(1) private Integer totalFloor;
    private String orientation;
    private String decoration;

    @NotNull @DecimalMin("0.0")
    private BigDecimal rent;

    @NotNull @DecimalMin("0.0")
    private BigDecimal deposit;

    private List<String> facilities;
    private String coverUrl;
    private List<String> imageUrls;
    @Size(max = 2000)
    private String description;

    /** 编辑时必传（乐观锁），新增忽略 */
    private Integer version;
}
