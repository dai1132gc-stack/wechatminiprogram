package com.rentalplatform.controller;

import com.rentalplatform.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 存活探针：验证工程可启动、统一响应体格式正确。 */

@RestController
public class PingController {

    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("pong");
    }
}
