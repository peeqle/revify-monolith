package com.revify.monolith.finance.service;


import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.commons.models.user.UserRole;
import com.revify.monolith.finance.RecipientProcessor;
import com.revify.monolith.finance.model.dto.PaymentMethodDTO;
import com.revify.monolith.finance.model.dto.RecipientTokenDTO;
import com.revify.monolith.finance.model.jpa.BePaidPaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.StripePaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.payment.PaymentToken;
import com.revify.monolith.finance.service.management.PaymentServiceResolver;
import com.revify.monolith.finance.service.management.StripeRecipientManagementService;
import com.revify.monolith.finance.service.repository.PaymentSystemRepository;
import com.revify.monolith.finance.service.repository.RecipientAccountRepository;
import com.revify.monolith.finance.service.repository.StripePaymentSystemRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.revify.monolith.commons.auth.sync.UserUtils.getUserId;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
@RequiredArgsConstructor

public class RecipientService {

    private final PaymentServiceResolver paymentServiceResolver;

    private final RecipientAccountRepository recipientAccountRepository;

    private final StripePaymentSystemRepository stripePaymentSystemRepository;

    private final PaymentSystemRepository paymentSystemRepository;

    private final StripeRecipientManagementService stripeRecipientManagementService;

    public Map<String, List<PaymentMethodDTO>> getUserPaymentMethods() {
        HashMap<String, List<PaymentMethodDTO>> paymentMethods = new HashMap<>();
        List<PaymentSystemAccount> paymentSystemAccounts = fetchForUser(getUserId());
        if (paymentSystemAccounts.isEmpty()) {
            return Collections.emptyMap();
        }

        for (PaymentSystemAccount paymentSystemAccount : paymentSystemAccounts) {
            if (paymentSystemAccount instanceof BePaidPaymentSystemAccount) {
                throw new UnsupportedOperationException();
            } else if (paymentSystemAccount instanceof StripePaymentSystemAccount) {
                PaymentMethodCollection associatedPaymentMethods = stripeRecipientManagementService.getAssociatedPaymentMethods(paymentSystemAccount.getAccountId());
                if (associatedPaymentMethods != null) {
                    associatedPaymentMethods.autoPagingIterable()
                            .forEach(method -> {
                                PaymentMethod.Card card = method.getCard();
                                if (card != null) {
                                    paymentMethods.computeIfAbsent(PaymentProcessor.STRIPE.getServiceName(), k -> new ArrayList<>()).add(
                                            PaymentMethodDTO.builder()
                                                    .id(method.getId())
                                                    .last4(card.getLast4())
                                                    .cardCountry(card.getCountry())
                                                    .displayBrand(card.getDisplayBrand())
                                                    .expYear(card.getExpYear())
                                                    .expMonth(card.getExpMonth())
                                                    .cvcCorrect(Objects.equals(card.getChecks().getCvcCheck(), "pass"))
                                                    .build()
                                    );
                                }
                            });
                }
            }
        }

        return paymentMethods;
    }

    public String prepareIntent(String paymentSystemUserId) {
        PageRequest page = PageRequest.of(0, 1);
        List<PaymentSystemAccount> byAccountId = paymentSystemRepository.findTopByAccountId(paymentSystemUserId, UserUtils.getUserId(), page);
        if (byAccountId == null || byAccountId.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Cannot find payment system account for user" + paymentSystemUserId);
        }
        PaymentSystemAccount top = byAccountId.getFirst();
        if (top instanceof StripePaymentSystemAccount stripeAccount) {
            try {
                return stripeRecipientManagementService.getSetupIntent(stripeAccount.getAccountId());
            } catch (StripeException e) {
                throw new ResponseStatusException(BAD_REQUEST, "Cannot process card creation request");
            }
        } else if (top instanceof BePaidPaymentSystemAccount bePaidAccount) {
            throw new UnsupportedOperationException();
        }
        throw new RuntimeException("Cannot cook");
    }

    public List<PaymentSystemAccount> fetchForUser(Long userId) {
        return paymentSystemRepository.findBySystemUserId(userId);
    }

    public Boolean currentUserHasActivePaymentAccount() {
        return paymentSystemRepository.existsBySystemUserIdAndIsCustomer(getUserId(), true);
    }

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
        RecipientProcessor<?, ?> recipientProcessor = paymentServiceResolver.resolveService(recipientTokenDTO.getPaymentProcessor());
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
        if (recipientCreation == null) {
            throw new IllegalArgumentException("Recipient creation cannot be null");
        }
        RecipientProcessor<?, ?> recipientProcessor = paymentServiceResolver.resolveServiceByCountry(recipientCreation.getCountryCode());
        if (recipientProcessor == null) {
            throw new UnsupportedOperationException(recipientCreation.getCountryCode() + " is not supported");
        }

        if (!recipientCreation.getUserRole().equals(UserRole.CLIENT)) {
            Object registeredCourier = recipientProcessor.registerCourier(recipientCreation);

            if (registeredCourier instanceof Account account) {

            }
        }

        Object registeredCustomer = recipientProcessor.registerCustomer(recipientCreation);

        if (registeredCustomer instanceof Customer account) {
            StripePaymentSystemAccount stripePaymentSystemAccount = new StripePaymentSystemAccount();
            stripePaymentSystemAccount.setSystemUserId(Long.valueOf(account.getMetadata().get("userId")));
            stripePaymentSystemAccount.setAccountId(account.getId());
            stripePaymentSystemAccount.setFirstName(recipientCreation.getFirstName());
            stripePaymentSystemAccount.setLastName(recipientCreation.getLastName());
            stripePaymentSystemAccount.setName(account.getName());
            stripePaymentSystemAccount.setEmail(account.getEmail());
            stripePaymentSystemAccount.setPhoneNumber(account.getPhone());

            PaymentSystemAccount.Address address = new PaymentSystemAccount.Address();
            address.setCity(account.getAddress().getCity());
            address.setCountry(account.getAddress().getCountry());
            address.setPostalCode(account.getAddress().getPostalCode());
            address.setState(recipientCreation.getRegion());
            address.setAddressLine(account.getAddress().getLine1() + " " + account.getAddress().getLine2());
            stripePaymentSystemAccount.setAddress(address);

            stripePaymentSystemAccount.setDobDay(recipientCreation.getDobDay());
            stripePaymentSystemAccount.setDobMonth(recipientCreation.getDobMonth());
            stripePaymentSystemAccount.setDobYear(recipientCreation.getDobYear());

            stripePaymentSystemAccount.setIsDeleted(account.getDeleted() != null && account.getDeleted());
            stripePaymentSystemAccount.setIsActive(account.getDeleted() == null || !account.getDeleted());
            stripePaymentSystemAccount.setCreatedAt(account.getCreated());

            stripePaymentSystemRepository.save(stripePaymentSystemAccount);
        }
    }
}
