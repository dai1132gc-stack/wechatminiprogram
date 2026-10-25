package com.rentalplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 房屋租赁与室内环境监测系统 - 后端服务入口。
 * 分层职责边界见项目 README / 技术方案 §3.1：Controller 只做参数与返回，
 * Service 做业务编排与事务，Mapper 只做数据访问。
 */
@SpringBootApplication
@EnableScheduling
public class RentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalApplication.class, args);
    }
}
