package com.api.stuv.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Proxy;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI stuvAPI() {
        String jwtScheme = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);

        Components components =
                new Components()
                        .addSecuritySchemes(
                                jwtScheme,
                                new SecurityScheme()
                                        .name(jwtScheme)
                                        .type(Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(getinfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info getinfo() {
        return new Info()
                .title("STUV API")
                .description("우아모 팀의 STUV API 명세")
                .version("0.0.1");
    }
}
