package com.teamchallenge.marketplace.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@Configuration
@Profile("!test")
public class SwaggerConfig {

    @Value(value = "${springdoc.server.url}")
    String serverURL;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Marketplace APIs")
                        .version("v1.0.0"))
                .servers(Collections.singletonList(new Server().url(serverURL)))
                .addSecurityItem(new SecurityRequirement().addList("marketplace"))
                .components(new Components()
                        .addSecuritySchemes("marketplace",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

}
