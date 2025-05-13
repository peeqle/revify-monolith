package com.revify.monolith.document.service;

import com.revify.monolith.document.models.LicenseModel;
import com.revify.monolith.document.service.data.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseModelService {

    private final LicenseRepository licenseRepository;

    public Optional<LicenseModel> getLicenseByRegion(String region) {
        return licenseRepository.getLastLicenseForRegion(region);
    }
}
