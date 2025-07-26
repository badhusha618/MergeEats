package com.mergeeats.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Advanced OpenAPI Configuration for MergeEats User Service
 * Provides comprehensive API documentation for user management operations
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .components(createComponents())
                .security(createSecurityRequirements())
                .externalDocs(createExternalDocumentation());
    }

    private Info createApiInfo() {
        return new Info()
                .title("MergeEats User Service API")
                .description("**Comprehensive User Management Service for MergeEats Platform**\n\n" +
                        "This service handles all user-related operations including:\n\n" +
                        "## Core Features\n" +
                        "- **User Registration**: Create new customer and restaurant owner accounts\n" +
                        "- **Authentication**: JWT-based login and token management\n" +
                        "- **Profile Management**: Update user profiles, preferences, and settings\n" +
                        "- **Address Management**: Manage delivery addresses and locations\n" +
                        "- **Role Management**: Handle different user roles (Customer, Restaurant Owner, Admin)\n" +
                        "- **Account Security**: Password management, two-factor authentication\n" +
                        "- **User Preferences**: Dietary restrictions, favorite cuisines, notification settings\n\n" +
                        "## User Types\n" +
                        "- **Customers**: End users who place orders\n" +
                        "- **Restaurant Owners**: Business users who manage restaurants\n" +
                        "- **Delivery Partners**: Users who deliver orders\n" +
                        "- **Admins**: Platform administrators\n\n" +
                        "## Security\n" +
                        "All endpoints are secured with JWT tokens. Use the 'Authorize' button to set your token.")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats User Service Team")
                .email("user-service@mergeeats.com")
                .url("https://mergeeats.com/developers/user-service");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Development Server"),
                new Server()
                        .url("https://user-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://user-service.mergeeats.com")
                        .description("Production Server")
        );
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header using the Bearer scheme. " +
                                   "Enter 'Bearer' [space] and then your token in the text input below.")
                )
                .addSecuritySchemes("apiKey",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API Key for service-to-service communication")
                );
    }

    private List<SecurityRequirement> createSecurityRequirements() {
        return List.of(
                new SecurityRequirement().addList("bearerAuth"),
                new SecurityRequirement().addList("apiKey")
        );
    }

    private ExternalDocumentation createExternalDocumentation() {
        return new ExternalDocumentation()
                .description("User Service Documentation")
                .url("https://docs.mergeeats.com/services/user-service");
    }
}