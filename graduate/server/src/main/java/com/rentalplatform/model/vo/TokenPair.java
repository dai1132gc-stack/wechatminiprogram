package com.rentalplatform.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 刷新令牌出参（openapi.yaml TokenPair） */
@Data
@AllArgsConstructor
public class TokenPair {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
