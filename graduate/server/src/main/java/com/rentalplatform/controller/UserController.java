package com.rentalplatform.controller;

import com.rentalplatform.common.result.R;
import com.rentalplatform.model.vo.UserInfo;
import com.rentalplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "当前用户")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public R<UserInfo> me() {
        return R.ok(userService.getCurrentUser());
    }

    @Operation(summary = "当前模式与有效合同（小程序双模式判定）")
    @GetMapping("/me/mode")
    public R<UserService.CurrentMode> mode() {
        return R.ok(userService.getCurrentMode());
    }
}
