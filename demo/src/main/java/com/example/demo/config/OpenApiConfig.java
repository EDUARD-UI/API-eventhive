package com.example.demo.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    // Información general del proyecto visible en la cabecera de Swagger UI
    @Bean
    public OpenAPI eventhiveOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EventHive API")
                        .description("Documentación de endpoints del sistema de gestión de eventos")
                        .version("1.0.0"));
    }

    // Grupo: endpoints públicos (no requieren autenticación)
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("publicos")
                .displayName("Endpoints Públicos")
                .pathsToMatch(
                        "/api/auth/**",
                        "/api/boletos/**"
                )
                .build();
    }
}