package com.mergeeats.deliveryservice.config;

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
 * Advanced OpenAPI Configuration for MergeEats Delivery Service
 * Provides comprehensive API documentation for delivery management and tracking
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
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
                .title("MergeEats Delivery Service API")
                .description("**Advanced Delivery Management Service for MergeEats Platform**\n\n" +
                        "This service handles all delivery-related operations from assignment to completion:\n\n" +
                        "## Core Features\n" +
                        "- **Delivery Assignment**: Intelligent matching of orders to delivery partners\n" +
                        "- **Real-time Tracking**: GPS-based location tracking and ETA calculations\n" +
                        "- **Partner Management**: Onboarding, verification, and management of delivery partners\n" +
                        "- **Route Optimization**: Efficient delivery route planning and optimization\n" +
                        "- **Delivery Analytics**: Performance metrics and delivery insights\n" +
                        "- **Proof of Delivery**: Photo confirmation and delivery verification\n" +
                        "- **Multi-order Delivery**: Support for batched deliveries\n\n" +
                        "## Delivery States\n" +
                        "- **ASSIGNED**: Delivery partner assigned to order\n" +
                        "- **PICKED_UP**: Order picked up from restaurant\n" +
                        "- **IN_TRANSIT**: Order is being delivered\n" +
                        "- **DELIVERED**: Order successfully delivered\n" +
                        "- **FAILED**: Delivery attempt failed\n" +
                        "- **RETURNED**: Order returned to restaurant\n\n" +
                        "## Partner Types\n" +
                        "- **Bicycle**: Eco-friendly short-distance delivery\n" +
                        "- **Motorcycle**: Fast urban delivery\n" +
                        "- **Car**: Long-distance and bulk orders\n" +
                        "- **Walking**: Ultra-short distance delivery\n\n" +
                        "## Advanced Features\n" +
                        "- **Smart Matching**: AI-powered partner-order matching\n" +
                        "- **Dynamic Pricing**: Surge pricing during peak hours\n" +
                        "- **Delivery Zones**: Geographic coverage management\n" +
                        "- **Partner Ratings**: Customer feedback and partner scoring\n" +
                        "- **Emergency Support**: 24/7 delivery support system\n\n" +
                        "## Integration Points\n" +
                        "- **Order Service**: Order status updates and delivery coordination\n" +
                        "- **User Service**: Customer and partner authentication\n" +
                        "- **Payment Service**: Delivery fee processing and partner payouts\n" +
                        "- **Notification Service**: Real-time delivery updates")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats Delivery Service Team")
                .email("delivery-service@mergeeats.com")
                .url("https://mergeeats.com/developers/delivery-service");
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
                        .url("https://delivery-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://delivery-service.mergeeats.com")
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
                .description("Delivery Service Documentation")
                .url("https://docs.mergeeats.com/services/delivery-service");
    }
}