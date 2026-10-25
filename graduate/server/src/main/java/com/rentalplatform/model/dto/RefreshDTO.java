package com.rentalplatform.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** POST /auth/refresh 入参：refresh token 仅在本接口以请求体携带 */
@Data
public class RefreshDTO {

    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
