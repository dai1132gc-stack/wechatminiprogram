package com.rentalplatform.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 审核房源入参（openapi.yaml HouseAuditDTO）。驳回时 remark 必填（Service 校验）。 */
@Data
public class HouseAuditDTO {

    @NotBlank
    private String auditStatus; // APPROVED / REJECTED

    @Size(max = 200)
    private String remark;
}
