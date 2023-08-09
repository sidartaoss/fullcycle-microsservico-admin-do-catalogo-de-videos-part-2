package com.fullcycle.admin.catalogo.infrastructure.configuration.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Video Catalog Administration API", description = "Backend application API for managing the video catalog", version = "v1"))
public class OpenAPIConfig {
}
