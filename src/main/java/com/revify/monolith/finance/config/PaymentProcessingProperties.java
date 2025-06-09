package com.revify.monolith.finance.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor

@ConfigurationProperties(prefix = "payment")
public class PaymentProcessingProperties {
    private Processors processors;

    private Providers credentials;

    public record Processors(String global) {
    }

    public record Providers(Credentials stripe, Credentials bePaid) {
    }

    public record Credentials(String pub, String sec) {
    }
}
