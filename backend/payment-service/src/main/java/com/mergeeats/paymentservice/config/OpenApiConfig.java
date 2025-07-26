package com.mergeeats.paymentservice.config;

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
 * Advanced OpenAPI Configuration for MergeEats Payment Service
 * Provides comprehensive API documentation for payment processing and financial operations
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8085}")
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
                .title("MergeEats Payment Service API")
                .description("**Secure Payment Processing Service for MergeEats Platform**\n\n" +
                        "This service handles all financial transactions with enterprise-grade security:\n\n" +
                        "## Core Features\n" +
                        "- **Payment Processing**: Secure credit card, debit card, and digital wallet payments\n" +
                        "- **Multi-Gateway Support**: Integration with multiple payment gateways for redundancy\n" +
                        "- **Refund Management**: Automated and manual refund processing\n" +
                        "- **Split Payments**: Support for group orders and payment splitting\n" +
                        "- **Recurring Payments**: Subscription and recurring payment handling\n" +
                        "- **Payment Analytics**: Transaction reporting and financial insights\n" +
                        "- **Fraud Detection**: Real-time fraud detection and prevention\n\n" +
                        "## Payment Methods\n" +
                        "- **Credit/Debit Cards**: Visa, MasterCard, American Express, Discover\n" +
                        "- **Digital Wallets**: PayPal, Apple Pay, Google Pay, Samsung Pay\n" +
                        "- **Bank Transfers**: ACH, wire transfers, and direct bank payments\n" +
                        "- **Cryptocurrency**: Bitcoin, Ethereum, and other digital currencies\n" +
                        "- **Buy Now Pay Later**: Klarna, Afterpay, and similar services\n\n" +
                        "## Payment States\n" +
                        "- **PENDING**: Payment initiated, awaiting processing\n" +
                        "- **PROCESSING**: Payment being processed by gateway\n" +
                        "- **COMPLETED**: Payment successfully processed\n" +
                        "- **FAILED**: Payment processing failed\n" +
                        "- **CANCELLED**: Payment cancelled by user or system\n" +
                        "- **REFUNDED**: Payment refunded to customer\n\n" +
                        "## Security Features\n" +
                        "- **PCI DSS Compliance**: Full PCI DSS Level 1 compliance\n" +
                        "- **Tokenization**: Secure card tokenization for repeat payments\n" +
                        "- **3D Secure**: Enhanced authentication for card payments\n" +
                        "- **Encryption**: End-to-end encryption of sensitive data\n" +
                        "- **Audit Logging**: Comprehensive transaction audit trails\n\n" +
                        "## Integration Points\n" +
                        "- **Order Service**: Order payment validation and processing\n" +
                        "- **User Service**: Customer payment method management\n" +
                        "- **Delivery Service**: Delivery fee processing and partner payouts\n" +
                        "- **Notification Service**: Payment status notifications")
                .version("2.0.0")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://mergeeats.com/terms");
    }

    private Contact createContact() {
        return new Contact()
                .name("MergeEats Payment Service Team")
                .email("payment-service@mergeeats.com")
                .url("https://mergeeats.com/developers/payment-service");
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
                        .url("https://payment-service-staging.mergeeats.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://payment-service.mergeeats.com")
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
                .description("Payment Service Documentation")
                .url("https://docs.mergeeats.com/services/payment-service");
    }
}