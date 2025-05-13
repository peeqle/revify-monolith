package com.revify.monolith.billing.resource;

import com.revify.monolith.billing.model.Billing;
import com.revify.monolith.billing.model.dto.BillingSearchDTO;
import com.revify.monolith.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/fetch-all")
    public ResponseEntity<Page<Billing>> fetchAllBilling(@RequestBody BillingSearchDTO value, Pageable pageable) {
        return ResponseEntity.ok(billingService.readAvailableBilling(value, pageable));
    }

    //todo make handler for callback from payment service to change Billing status
}
