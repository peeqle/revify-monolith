package com.revify.monolith.finance.config;

import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.finance.PaymentService;
import com.revify.monolith.finance.config.properties.PaymentProcessingProperties;
import com.revify.monolith.finance.service.payment.BePaidPaymentServiceService;
import com.revify.monolith.finance.service.payment.StripePaymentServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FinanceConfiguration {

    private final PaymentProcessingProperties paymentProcessingProperties;

    @Bean
    public PaymentService<?> globalPaymentProcessor() {
        if (paymentProcessingProperties.getGlobal().equals(PaymentProcessor.BE_PAID.getServiceName())) {
            return new BePaidPaymentServiceService();
        } else if (paymentProcessingProperties.getGlobal().equals(PaymentProcessor.STRIPE.getServiceName())) {
            return new StripePaymentServiceService();
        } else if (paymentProcessingProperties.getGlobal().equals(PaymentProcessor.BANK_PROCESSING.getServiceName())) {
            throw new UnsupportedOperationException("Bank is not currently supported");
        }

        throw new IllegalArgumentException("Unsupported payment processor type");
    }
}
