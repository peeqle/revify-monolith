package com.revify.monolith.finance.service;


import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.RecipientProcessor;
import com.revify.monolith.finance.model.dto.RecipientTokenDTO;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.StripePaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.payment.PaymentToken;
import com.revify.monolith.finance.service.management.PaymentServiceResolver;
import com.revify.monolith.finance.service.repository.RecipientAccountRepository;
import com.revify.monolith.finance.service.repository.StripePaymentSystemRepository;
import com.stripe.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.revify.monolith.commons.auth.sync.UserUtils.getUserId;


@Service
@RequiredArgsConstructor

public class RecipientService {

    private final PaymentServiceResolver paymentServiceResolver;

    private final RecipientAccountRepository recipientAccountRepository;

    private final StripePaymentSystemRepository stripePaymentSystemRepository;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<PaymentToken> fetchExistingForRecipient(Pageable pageable) {
        try {
            return recipientAccountRepository.findAllByAccountUserId(getUserId(), pageable);
        } catch (UnauthorizedAccessError e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void removePaymentToken(Long id) throws UnauthorizedAccessError {
        Optional<PaymentToken> byId = recipientAccountRepository.findById(id);
        if (byId.isPresent()) {
            boolean equals = Objects.equals(byId.get().getPaymentSystemAccount().getSystemUserId(), getUserId());
            if (equals) {
                recipientAccountRepository.deleteById(id);
            } else {
                throw new UnauthorizedAccessError("Cannot delete recipient account with id " + id);
            }
        }
    }

    @Transactional
    public void registerRecipientToken(RecipientTokenDTO recipientTokenDTO) {
        RecipientProcessor<?> recipientProcessor = paymentServiceResolver.resolveService(recipientTokenDTO.getPaymentProcessor());
        if (recipientProcessor == null) {
            throw new UnsupportedOperationException(recipientTokenDTO.getPaymentProcessor() + " is not supported");
        }
        boolean paymentValid = recipientProcessor.paymentMethodIsValid(recipientTokenDTO.getPaymentMethodId());
        if (!paymentValid) {
            throw new IllegalArgumentException(recipientTokenDTO.getPaymentMethodId() + " is not valid payment method.");
        }
        PaymentToken recipientAccount = new PaymentToken();
        recipientAccount.setCardToken(recipientTokenDTO.getPaymentMethodId());
        recipientAccount.setPaymentProcessor(recipientTokenDTO.getPaymentProcessor());

        recipientAccountRepository.save(recipientAccount);
    }

    /**
     * Create user account in payment system according to user residence location
     *
     * @param recipientCreation
     */
    @Transactional
    public void registerRecipient(RecipientCreation recipientCreation) throws Exception {
        RecipientProcessor<?> recipientProcessor = paymentServiceResolver.resolveServiceByCountry(recipientCreation.getCountryCode());
        if (recipientProcessor == null) {
            throw new UnsupportedOperationException(recipientCreation.getCountryCode() + " is not supported");
        }

        Object register = recipientProcessor.register(recipientCreation);

        if (register instanceof Account account) {
            StripePaymentSystemAccount stripePaymentSystemAccount = new StripePaymentSystemAccount();
            stripePaymentSystemAccount.setAccountId(account.getId());
            stripePaymentSystemAccount.setFirstName(account.getIndividual().getFirstName());
            stripePaymentSystemAccount.setLastName(account.getIndividual().getLastName());
            stripePaymentSystemAccount.setEmail(account.getIndividual().getEmail());

            PaymentSystemAccount.Address address = new PaymentSystemAccount.Address();
            address.setCity(account.getIndividual().getAddress().getCity());
            address.setCountry(account.getIndividual().getAddress().getCountry());
            address.setPostalCode(account.getIndividual().getAddress().getPostalCode());
            address.setAddressLine(account.getIndividual().getAddress().getLine1() + " " + account.getIndividual().getAddress().getLine2());

            stripePaymentSystemAccount.setAddress(address);
            stripePaymentSystemAccount.setIsDeleted(account.getDeleted());
            stripePaymentSystemAccount.setIsActive(!account.getDeleted());

            stripePaymentSystemAccount.setCreatedAt(account.getCreated());
            stripePaymentSystemRepository.save(stripePaymentSystemAccount);
        }
    }
}
