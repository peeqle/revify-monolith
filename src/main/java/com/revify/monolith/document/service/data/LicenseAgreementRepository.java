package com.revify.monolith.document.service.data;

import com.revify.monolith.document.models.LicenseAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseAgreementRepository extends JpaRepository<LicenseAgreement, Long> {
}
