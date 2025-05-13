package com.revify.monolith.document.resource;

import com.revify.monolith.document.service.LicenseModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseModelService licenseService;

    @GetMapping("/region")
    public ResponseEntity<?> getLicenseForRegion(@RequestParam String region) {
        return ResponseEntity.ok(licenseService.getLicenseByRegion(region));
    }
}
