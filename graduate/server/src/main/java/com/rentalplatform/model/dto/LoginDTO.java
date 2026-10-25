package com.rentalplatform.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** POST /auth/login 入参 */
@Data
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度 6-32 位")
    private String password;
}
