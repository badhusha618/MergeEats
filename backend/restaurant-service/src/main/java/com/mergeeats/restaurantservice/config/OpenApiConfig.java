package com.mergeeats.restaurantservice.config;

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
 * Advanced OpenAPI Configuration for MergeEats Restaurant Service
 * Provides comprehensive API documentation for restaurant and menu management
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
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
                .title("MergeEats Restaurant Service API")
                .description("**Comprehensive Restaurant Management Service for MergeEats Platform**\n\n" +
                        "This service manages restaurants, menus, and food items across the platform:\n\n" +
                        "## Core Features\n" +
                        "- **Restaurant Management**: Create, update, and manage restaurant profiles\n" +
                        "- **Menu Management**: Comprehensive menu and item management system\n" +
                        "- **Restaurant Discovery**: Search and filter restaurants by various criteria\n" +
                        "- **Operating Hours**: Manage restaurant availability and special hours\n" +
                        "- **Cuisine Categories**: Organize restaurants by cuisine types\n" +
                        "- **Restaurant Analytics**: Performance metrics and insights\n" +
                        "- **Rating & Reviews**: Customer feedback and rating management\n\n" +
                        "## Restaurant Types\n" +
                        "- **Fast Food**: Quick service restaurants\n" +
                        "- **Fine Dining**: Premium dining experiences\n" +
                        "- **Casual Dining**: Family-friendly restaurants\n" +
                        "- **Food Trucks**: Mobile food vendors\n" +
                        "- **Cloud Kitchens**: Delivery-only restaurants\n\n" +
                        "## Menu Management\n" +
                        "- **Categories**: Organize items into categories (Appetizers, Mains, Desserts)\n" +
                        "- **Item Variants**: Handle size, customizations, and add-ons\n" +
                        "- **Pricing**: Dynamic pricing and promotional offers\n" +
                        "- **Availability**: Real-time item availability management\n" +
                        "- **Nutritional Info**: Calories, allergens, and dietary information\n\n" +
                        "## Integration Points\n" +
                        "- **User Service**: Restaurant owner authentication and management\n" +
                        "- **Order Service**: Menu item validation and availability\n" +
                        "- **Delivery Service**: Restaurant location and delivery zones")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats Restaurant Service Team")
                .email("restaurant-service@mergeeats.com")
                .url("https://mergeeats.com/developers/restaurant-service");
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
                        .url("https://restaurant-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://restaurant-service.mergeeats.com")
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
                .description("Restaurant Service Documentation")
                .url("https://docs.mergeeats.com/services/restaurant-service");
    }
}