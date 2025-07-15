package com.revify.monolith;

import com.revify.monolith.finance.config.PaymentProcessingProperties;
import com.revify.monolith.finance.model.exc.PaymentServiceInitializationException;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@EnableJpaRepositories
@SpringBootApplication
@EntityScan(basePackages = {"com.revify.monolith"})

@RequiredArgsConstructor
public class RevifyMonolithApplication {

    private final PaymentProcessingProperties paymentProcessingProperties;

    public static void main(String[] args) {
        SpringApplication.run(RevifyMonolithApplication.class, args);
    }

    @PostConstruct
    public void init() throws PaymentServiceInitializationException {
        PaymentProcessingProperties.Credentials stripe = paymentProcessingProperties.getCredentials()
                .stripe();
        if (stripe != null) {
            Stripe.apiKey = stripe.sec();
            return;
        }
        throw new PaymentServiceInitializationException("Cannot configure Stripe credentials from configuration, stripe is null");
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> npeHandler() {
        return ResponseEntity.notFound().build();
    }
}
