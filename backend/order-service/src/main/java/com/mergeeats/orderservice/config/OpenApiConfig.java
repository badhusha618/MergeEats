package com.mergeeats.orderservice.config;

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
 * Advanced OpenAPI Configuration for MergeEats Order Service
 * Provides comprehensive API documentation for order management operations
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
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
                .title("MergeEats Order Service API")
                .description("**Advanced Order Management Service for MergeEats Platform**\n\n" +
                        "This service handles the complete order lifecycle from creation to delivery:\n\n" +
                        "## Core Features\n" +
                        "- **Order Creation**: Create new orders with items, customizations, and preferences\n" +
                        "- **Order Management**: Update, modify, and cancel orders\n" +
                        "- **Order Tracking**: Real-time order status updates and tracking\n" +
                        "- **Cart Management**: Shopping cart operations and persistence\n" +
                        "- **Order History**: Access to past orders and reordering functionality\n" +
                        "- **Group Orders**: Support for group ordering and splitting\n" +
                        "- **Special Instructions**: Handle dietary restrictions and special requests\n\n" +
                        "## Order States\n" +
                        "- **PENDING**: Order created, awaiting restaurant confirmation\n" +
                        "- **CONFIRMED**: Restaurant confirmed the order\n" +
                        "- **PREPARING**: Order is being prepared\n" +
                        "- **READY**: Order ready for pickup/delivery\n" +
                        "- **OUT_FOR_DELIVERY**: Order is being delivered\n" +
                        "- **DELIVERED**: Order successfully delivered\n" +
                        "- **CANCELLED**: Order was cancelled\n\n" +
                        "## Integration Points\n" +
                        "- **User Service**: Customer and restaurant owner validation\n" +
                        "- **Restaurant Service**: Menu items and availability\n" +
                        "- **Payment Service**: Payment processing and validation\n" +
                        "- **Delivery Service**: Delivery assignment and tracking\n" +
                        "- **Notification Service**: Order status notifications")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats Order Service Team")
                .email("order-service@mergeeats.com")
                .url("https://mergeeats.com/developers/order-service");
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
                        .url("https://order-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://order-service.mergeeats.com")
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
                .description("Order Service Documentation")
                .url("https://docs.mergeeats.com/services/order-service");
    }
}