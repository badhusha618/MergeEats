package com.mergeeats.userservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MergeEats User Service API")
                        .description("API for user management and authentication")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MergeEats Team")
                                .email("support@mergeeats.com")
                                .url("https://mergeeats.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Development Server"),
                        new Server().url("https://api.mergeeats.com").description("Production Server")
                ))
                .addTagsItem(new Tag().name("User").description("User account and profile operations"))
                .addTagsItem(new Tag().name("Auth").description("Authentication and authorization endpoints"))
                .externalDocs(new ExternalDocumentation()
                        .description("MergeEats API Documentation")
                        .url("https://docs.mergeeats.com"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .schemaRequirement("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer authentication for secured endpoints"));
    }
} 