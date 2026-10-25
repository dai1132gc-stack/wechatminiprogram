package com.rentalplatform.controller;

import com.rentalplatform.common.result.R;
import com.rentalplatform.common.util.IpUtils;
import com.rentalplatform.model.dto.LoginDTO;
import com.rentalplatform.model.dto.RefreshDTO;
import com.rentalplatform.model.vo.LoginVO;
import com.rentalplatform.model.vo.TokenPair;
import com.rentalplatform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Web 账号密码登录")
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody @Valid LoginDTO dto, HttpServletRequest request) {
        return R.ok(authService.login(dto, IpUtils.clientIp(request)));
    }

    @Operation(summary = "刷新令牌（一次性轮换 + 30 秒幂等宽限）")
    @PostMapping("/refresh")
    public R<TokenPair> refresh(@RequestBody @Valid RefreshDTO dto) {
        return R.ok(authService.refresh(dto.getRefreshToken()));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public R<Void> logout(@RequestBody RefreshDTO dto, HttpServletRequest request) {
        String accessToken = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            accessToken = header.substring(7);
        }
        authService.logout(accessToken, dto.getRefreshToken());
        return R.ok();
    }
}
