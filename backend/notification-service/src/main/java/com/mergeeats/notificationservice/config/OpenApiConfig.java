package com.mergeeats.notificationservice.config;

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
 * Advanced OpenAPI Configuration for MergeEats Notification Service
 * Provides comprehensive API documentation for messaging and notification operations
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8086}")
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
                .title("MergeEats Notification Service API")
                .description("**Advanced Notification and Messaging Service for MergeEats Platform**\n\n" +
                        "This service handles all communication and notification operations across the platform:\n\n" +
                        "## Core Features\n" +
                        "- **Multi-Channel Messaging**: Email, SMS, push notifications, and in-app messages\n" +
                        "- **Real-time Notifications**: Instant delivery of time-sensitive updates\n" +
                        "- **Template Management**: Dynamic message templates with personalization\n" +
                        "- **Notification Preferences**: User-controlled notification settings\n" +
                        "- **Delivery Tracking**: Comprehensive delivery status and analytics\n" +
                        "- **Scheduled Notifications**: Time-based and event-triggered messaging\n" +
                        "- **Bulk Messaging**: Mass communication campaigns and announcements\n\n" +
                        "## Notification Channels\n" +
                        "- **Push Notifications**: Mobile app and web push notifications\n" +
                        "- **Email**: Transactional and marketing emails with rich HTML content\n" +
                        "- **SMS**: Text messages for critical updates and OTP delivery\n" +
                        "- **In-App**: Real-time in-application notifications and messages\n" +
                        "- **WhatsApp**: Business messaging via WhatsApp Business API\n" +
                        "- **Webhooks**: API callbacks for system integrations\n\n" +
                        "## Notification Types\n" +
                        "- **Order Updates**: Order confirmation, preparation, delivery status\n" +
                        "- **Payment Alerts**: Payment confirmations, failures, and refunds\n" +
                        "- **Promotional**: Offers, discounts, and marketing campaigns\n" +
                        "- **System Alerts**: Maintenance, downtime, and security notifications\n" +
                        "- **Account**: Registration, login, password reset, and profile updates\n" +
                        "- **Delivery**: Real-time delivery tracking and partner updates\n\n" +
                        "## Advanced Features\n" +
                        "- **Smart Delivery**: Optimal timing based on user behavior\n" +
                        "- **A/B Testing**: Message content and timing optimization\n" +
                        "- **Personalization**: Dynamic content based on user preferences\n" +
                        "- **Rate Limiting**: Prevents notification spam and respects user limits\n" +
                        "- **Fallback Channels**: Automatic retry via alternative channels\n" +
                        "- **Analytics**: Delivery rates, open rates, and engagement metrics\n\n" +
                        "## Integration Points\n" +
                        "- **Order Service**: Order status and lifecycle notifications\n" +
                        "- **User Service**: Account and authentication notifications\n" +
                        "- **Payment Service**: Transaction and payment status alerts\n" +
                        "- **Delivery Service**: Real-time delivery tracking updates")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats Notification Service Team")
                .email("notification-service@mergeeats.com")
                .url("https://mergeeats.com/developers/notification-service");
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
                        .url("https://notification-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://notification-service.mergeeats.com")
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
                .description("Notification Service Documentation")
                .url("https://docs.mergeeats.com/services/notification-service");
    }
}