package com.olivares.api_rest_security_jwt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String titulo;

    @Value("${application-description}")
    private String descripcion;

    private SecurityScheme createBearerAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP).name("Bearer Authentication").scheme("bearer");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Authentication", createBearerAPIKeyScheme()))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .info(new Info()
                        .title(titulo)
                        .description(descripcion)
                        .termsOfService("terms")
                        .contact(new Contact().email("@example.dev"))
                        .license(new License().name("GNU"))
                        .version("1.0")
                );
    }

}