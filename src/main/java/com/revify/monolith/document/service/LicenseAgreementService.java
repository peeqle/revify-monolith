package com.revify.monolith.document.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.document.models.LicenseAgreement;
import com.revify.monolith.document.models.LicenseModel;
import com.revify.monolith.document.service.data.LicenseAgreementRepository;
import com.revify.monolith.document.service.data.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LicenseAgreementService {

    private final LicenseAgreementRepository licenseAgreementRepository;

    private final LicenseRepository licenseRepository;

    public Boolean acceptUserAgreement(Long licenseModelId) throws UnauthorizedAccessError {
        if (licenseRepository.existsById(licenseModelId)) {
            LicenseAgreement licenseAgreement = new LicenseAgreement();
            licenseAgreement.setUserId(UserUtils.getUserId());
            licenseAgreement.setLicense(new LicenseModel(licenseModelId));
            licenseAgreement.setCreatedAt(Instant.now().toEpochMilli());

            licenseAgreementRepository.save(licenseAgreement);
            return true;
        }
        throw new IllegalArgumentException("License agreement not found");
    }
}
