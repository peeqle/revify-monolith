package com.revify.monolith.document.resource;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.document.service.LicenseAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/agreement")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class AgreementController {

    private final LicenseAgreementService licenseAgreementService;

    @PostMapping
    private ResponseEntity<?> userConsent(@RequestParam Long licenseModelId) throws UnauthorizedAccessError {
        return ResponseEntity.ok(licenseAgreementService.acceptUserAgreement(licenseModelId));
    }
}
