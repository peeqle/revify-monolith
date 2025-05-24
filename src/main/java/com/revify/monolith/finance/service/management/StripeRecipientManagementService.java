package com.revify.monolith.finance.service.management;


import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.RecipientProcessor;
import com.revify.monolith.finance.config.properties.PaymentProcessingProperties;
import com.revify.monolith.finance.model.exc.PaymentServiceInitializationException;
import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class StripeRecipientManagementService implements RecipientProcessor<Customer, Account> {

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
    public Customer registerCustomer(RecipientCreation recipientCreation) {

        CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                .setEmail(recipientCreation.getEmail())
                .setName(recipientCreation.getFirstName() + " " + recipientCreation.getLastName())
                .setBalance(0L)
                .setAddress(CustomerCreateParams.Address.builder()
                        .setCountry(recipientCreation.getCountryCode())
                        .setState(recipientCreation.getRegion())
                        .setCity(recipientCreation.getCity())
                        .setCountry(recipientCreation.getCountryCode())
                        .setPostalCode(recipientCreation.getPostalCode())
                        .build())
                .setPhone(recipientCreation.getPhone())
                .setTax(CustomerCreateParams.Tax.builder()
                        .setIpAddress(recipientCreation.getIp())
                        .setValidateLocation(CustomerCreateParams.Tax.ValidateLocation.DEFERRED)
                        .build())
                .putAllMetadata(Map.of("userId", recipientCreation.getUserId().toString()))
                .build();

        try {
            return Customer.create(customerCreateParams);
        } catch (Exception e) {
            // Something else happened, completely unrelated to Stripe
        }
        throw new RuntimeException("Failed to create customer " + recipientCreation.getEmail());
    }

    @Override
    public Account registerCourier(RecipientCreation recipientCreation) {
        AccountCreateParams accountCreateParams = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.CUSTOM)
                .setEmail(recipientCreation.getEmail())
                .setCountry(recipientCreation.getCountryCode())
                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                .setIndividual(AccountCreateParams.Individual.builder()
                        .setPhone(recipientCreation.getPhone())
                        .setFirstName(recipientCreation.getFirstName())
                        .setLastName(recipientCreation.getLastName())
                        .setDob(AccountCreateParams.Individual.Dob.builder()
                                .setDay(recipientCreation.getDobDay())
                                .setMonth(recipientCreation.getDobMonth())
                                .setYear(recipientCreation.getDobYear())
                                .build())
                        .setAddress(AccountCreateParams.Individual.Address.builder()
                                .setCountry(recipientCreation.getCountryCode())
                                .setState(recipientCreation.getRegion())
                                .setCity(recipientCreation.getCity())
                                .setCountry(recipientCreation.getCountryCode())
                                .setPostalCode(recipientCreation.getPostalCode())
                                .build())
                        .build())
                .setCapabilities(AccountCreateParams.Capabilities.builder()
                        .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                .setRequested(true)
                                .build())
                        .build())
                .setTosAcceptance(AccountCreateParams.TosAcceptance.builder()
                        .setIp(recipientCreation.getIp())
                        .setDate(Instant.now().getEpochSecond())
                        .setUserAgent(recipientCreation.getBrowserAccess())
                        .setServiceAgreement("recipient")
                        .build())
                .build();


        try {
            return Account.create(accountCreateParams);
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
