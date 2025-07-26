package com.mergeeats.apigateway.config;

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
 * Advanced OpenAPI Configuration for MergeEats API Gateway
 * Provides comprehensive API documentation with security, servers, and metadata
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:api-gateway}")
    private String applicationName;

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
                .title("MergeEats API Gateway")
                .description("**Advanced API Gateway for MergeEats Food Delivery Platform**\n\n" +
                        "This API Gateway serves as the central entry point for all MergeEats microservices, " +
                        "providing unified access to:\n\n" +
                        "- **User Management**: Authentication, profiles, and user operations\n" +
                        "- **Restaurant Services**: Restaurant listings, menus, and management\n" +
                        "- **Order Processing**: Order creation, tracking, and management\n" +
                        "- **Delivery Services**: Delivery partner management and tracking\n" +
                        "- **Payment Processing**: Secure payment handling and transactions\n" +
                        "- **Notifications**: Real-time notifications and messaging\n\n" +
                        "## Features\n" +
                        "- **Load Balancing**: Intelligent request routing\n" +
                        "- **Rate Limiting**: API usage control and throttling\n" +
                        "- **Security**: JWT-based authentication and authorization\n" +
                        "- **Monitoring**: Request tracing and performance metrics\n" +
                        "- **Circuit Breaker**: Fault tolerance and resilience\n\n" +
                        "## Authentication\n" +
                        "All protected endpoints require a valid JWT token in the Authorization header.")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats API Team")
                .email("api-support@mergeeats.com")
                .url("https://mergeeats.com/developers");
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
                        .url("https://api-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://api.mergeeats.com")
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
                .description("MergeEats Developer Documentation")
                .url("https://docs.mergeeats.com");
    }
}