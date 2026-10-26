package com.rentalplatform.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** POST /auth/wx-login 入参（openapi.yaml WxLoginDTO） */
@Data
public class WxLoginDTO {

    @NotBlank(message = "code 不能为空")
    private String code;

    /** 手机号加密数据（可选，getPhoneNumber 流程） */
    private String encryptedData;
    private String iv;
}
