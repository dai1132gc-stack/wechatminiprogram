package com.rentalplatform.model.query;

import lombok.Data;

import java.math.BigDecimal;

/** GET /houses 查询条件（openapi.yaml）。 */
@Data
public class HouseQuery {

    private String keyword;
    private String district;
    private String layout;
    private BigDecimal rentMin;
    private BigDecimal rentMax;
    private String auditStatus;
    private String rentStatus;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
