package com.revify.monolith.document.service.data;


import com.revify.monolith.document.models.LicenseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseModel, Long> {

    @Query("select LicenseModel from LicenseModel m where m.region = :region order by m.createdAt desc limit 1")
    Optional<LicenseModel> getLastLicenseForRegion(String region);
}
