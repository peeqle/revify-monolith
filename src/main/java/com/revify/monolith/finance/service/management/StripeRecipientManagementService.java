package com.revify.monolith.finance.service.management;


import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.RecipientProcessor;
import com.revify.monolith.finance.config.properties.PaymentProcessingProperties;
import com.revify.monolith.finance.model.exc.PaymentServiceInitializationException;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Account;
import com.stripe.model.PaymentMethod;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class StripeRecipientManagementService implements RecipientProcessor<Account> {

    private final PaymentProcessingProperties paymentProcessingProperties;

    static {
        Stripe.enableTelemetry = false;
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

    @Override
    public Account register(RecipientCreation recipientCreation) {
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.CUSTOM)
                .setEmail(recipientCreation.getEmail())
                .setCountry(recipientCreation.getCountryCode())
                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                .setIndividual(AccountCreateParams.Individual.builder()
                        .setFirstName(recipientCreation.getFirstName())
                        .setLastName(recipientCreation.getLastName())
                        .setDob(AccountCreateParams.Individual.Dob.builder()
                                .setDay(recipientCreation.getDobDay())
                                .setMonth(recipientCreation.getDobMonth())
                                .setYear(recipientCreation.getDobYear())
                                .build())
                        .build())
                .setCapabilities(AccountCreateParams.Capabilities.builder()
                        .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                .setRequested(true)
                                .build())
                        .build())
                .build();


        try {
            return Account.create(params);
        } catch (CardException e) {
            // Since it's a decline, CardException will be caught
            System.out.println("Status is: " + e.getCode());
            System.out.println("Message is: " + e.getMessage());
        } catch (RateLimitException e) {
            // Too many requests made to the API too quickly
        } catch (InvalidRequestException e) {
            // Invalid parameters were supplied to Stripe's API
        } catch (AuthenticationException e) {
            // Authentication with Stripe's API failed
            // (maybe you changed API keys recently)
        } catch (StripeException e) {
            // Display a very generic error to the user, and maybe send
            // yourself an email
        } catch (Exception e) {
            // Something else happened, completely unrelated to Stripe
        }
        return null;
    }

    @Override
    public void attachPaymentMethod(String paymentMethodId, String accountId) throws Exception {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(accountId)
                .build();

        paymentMethod.attach(params);
    }

    @Override
    public boolean paymentMethodIsValid(String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            PaymentMethod.Card card = paymentMethod.getCard();
            Long expMonth = card.getExpMonth();
            Long expYear = card.getExpYear();

            java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
            java.time.YearMonth cardYearMonth = java.time.YearMonth.of(Math.toIntExact(expYear), Math.toIntExact(expMonth));

            return cardYearMonth.isBefore(currentYearMonth);
        } catch (Exception e) {
            log.warn("Cannot check payment method", e);
        }
        return false;
    }
}
