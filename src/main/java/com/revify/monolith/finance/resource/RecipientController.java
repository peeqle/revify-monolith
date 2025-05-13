package com.revify.monolith.finance.resource;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.finance.model.dto.RecipientTokenDTO;
import com.revify.monolith.finance.model.jpa.payment.PaymentToken;
import com.revify.monolith.finance.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/recipient")

@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class RecipientController {

    private final RecipientService recipientService;

    @PostMapping("/token")
    public void handleRecipientCardToken(@RequestBody RecipientTokenDTO recipientTokenDTO) {
        recipientService.registerRecipientToken(recipientTokenDTO);
    }

    @GetMapping("/fetch-tokens")
    public ResponseEntity<Page<PaymentToken>> fetchRecipientTokens(Pageable pageable) {
        return ResponseEntity.ok(recipientService.fetchExistingForRecipient(pageable));
    }

    @DeleteMapping("/remove-payment-method")
    public ResponseEntity<Object> removePaymentMethod(@RequestParam("payment-method-id") Long paymentMethodId) {
        if (paymentMethodId != null) {
            try {
                recipientService.removePaymentToken(paymentMethodId);
            } catch (UnauthorizedAccessError e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        return ResponseEntity.ok().build();
    }
}
