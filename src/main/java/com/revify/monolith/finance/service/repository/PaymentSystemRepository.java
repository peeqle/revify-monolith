package com.revify.monolith.finance.service.repository;

import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.StripePaymentSystemAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentSystemRepository extends JpaRepository<PaymentSystemAccount, Long> {

    List<PaymentSystemAccount> findBySystemUserId(Long userId);

    @Query("SELECT p FROM PaymentSystemAccount p WHERE " +
            "p.accountId = :paymentSystemAccountId AND " +
            "p.systemUserId = :userId AND " +
            "p.isActive = true AND " +
            "p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    List<PaymentSystemAccount> findTopByAccountId(
            String paymentSystemAccountId,
            Long userId,
            Pageable pageable);

    Boolean existsBySystemUserIdAndIsCustomer(Long systemUserId, Boolean isCustomer);
    Boolean existsBySystemUserIdAndIsReceiver(Long systemUserId, Boolean isReceiver);
}
