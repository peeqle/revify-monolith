package com.revify.monolith.finance.service.management;

import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.finance.RecipientProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentServiceResolver {

    private final BePaidRecipientManagementService bePaidRecipientManagementService;
    private final StripeRecipientManagementService stripeRecipientManagementService;

    public RecipientProcessor<?, ?> resolveService(PaymentProcessor paymentProcessor) {
        return switch (paymentProcessor) {
            case STRIPE -> stripeRecipientManagementService;
            case BE_PAID -> bePaidRecipientManagementService;
            case BANK_PROCESSING -> null;
        };
    }

    public RecipientProcessor<?, ?> resolveServiceByCountry(String countryCode) {
        return resolveService(PaymentProcessor.get(countryCode));
    }
}
