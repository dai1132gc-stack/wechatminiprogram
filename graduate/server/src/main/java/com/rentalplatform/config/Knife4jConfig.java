package com.rentalplatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI3 文档：启动后访问 http://localhost:8080/api/v1/doc.html
 * 导出的 PDF 可直接进论文附录。
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("房屋租赁与室内环境监测系统 API")
                        .description("契约唯一来源为 server/docs/openapi.yaml，本 UI 由代码注解生成，两者以 yaml 为准")
                        .version("0.1.0"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
