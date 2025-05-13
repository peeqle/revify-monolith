package com.revify.monolith.finance.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor

@ConfigurationProperties(prefix = "payment.processors")
public class PaymentProcessingProperties {

    private String global;
}
