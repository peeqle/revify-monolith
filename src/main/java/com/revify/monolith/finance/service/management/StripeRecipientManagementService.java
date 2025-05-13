package com.revify.monolith.finance.service.management;


import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.RecipientProcessor;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.PaymentMethod;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class StripeRecipientManagementService implements RecipientProcessor<Account> {

    static {
        Stripe.enableTelemetry = false;
    }

    @Override
    public Account register(RecipientCreation recipientCreation) throws StripeException {
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

        return Account.create(params);
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
