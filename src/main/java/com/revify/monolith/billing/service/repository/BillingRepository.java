package com.revify.monolith.billing.service.repository;

import com.revify.monolith.billing.model.Billing;
import com.revify.monolith.billing.model.InsuranceBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingRepository extends JpaRepository<Billing, UUID>, JpaSpecificationExecutor<Billing> {

    @Query("select i from InsuranceBilling i where i.insurance.id = :insuranceId")
    List<InsuranceBilling> findInsuranceBillingByInsuranceId(@Param("insuranceId") UUID insuranceId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM InsuranceBilling i WHERE i.insurance.id = :insuranceId")
    boolean existsByInsuranceId(@Param("insuranceId") UUID insuranceId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM ItemBilling i WHERE i.itemId = :itemId AND i.payerId = :payerId")
    boolean existsByItemIdAndPayerId(@Param("itemId") String itemId, @Param("payerId") Long payerId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM ItemPremiumBilling i WHERE i.itemPremiumId = :itemId AND i.payerId = :payerId")
    boolean existsByItemPremiumIdAndPayerId(@Param("itemId") String itemId, @Param("payerId") Long payerId);
}
