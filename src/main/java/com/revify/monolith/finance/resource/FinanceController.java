package com.revify.monolith.finance.resource;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.finance.model.dto.PaymentMethodDTO;
import com.revify.monolith.finance.model.jpa.BePaidPaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/finance")

@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FinanceController {

    private final RecipientService recipientService;

    @PostMapping("/intent")
    public void getCustomerIntents() {

    }

    @GetMapping("/client-exists")
    public ResponseEntity<?> validateCustomerExists() {
        return ResponseEntity.ok(recipientService.currentUserHasActivePaymentAccount());
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentAccountResponse>> fetchUserPaymentSystemAccount() {
        return ResponseEntity.ok(recipientService.fetchForUser(UserUtils.getUserId()).stream().map(PaymentAccountResponse::from).toList());
    }

    @GetMapping("/stripe/setup-intent")
    public ResponseEntity<String> fetchSetupIntent(@RequestParam("paymentSystemCustomerId") String paymentSystemCustomerId) {
        return ResponseEntity.ok(recipientService.prepareIntent(paymentSystemCustomerId));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<Map<String, List<PaymentMethodDTO>>> getUserPaymentMethods() {
        return ResponseEntity.ok(recipientService.getUserPaymentMethods());
    }


    public record PaymentAccountResponse(String paymentSystem, String name, String email, String phoneNumber,
                                         String paymentSystemId) {
        public static PaymentAccountResponse from(PaymentSystemAccount paymentSystemAccount) {
            String paymentSystem = "STRIPE";
            if (paymentSystemAccount instanceof BePaidPaymentSystemAccount) {
                paymentSystem = "BE_PAID";
            }

            return new PaymentAccountResponse(paymentSystem,
                    paymentSystemAccount.getName(),
                    paymentSystemAccount.getEmail(),
                    paymentSystemAccount.getPhoneNumber(),
                    paymentSystemAccount.getAccountId());
        }
    }
}
