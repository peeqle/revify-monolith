package com.revify.monolith.finance.service.repository;

import com.revify.monolith.finance.model.jpa.payment.Payment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p WHERE p.account.systemUserId = :userId" +
            " AND p.account.isActive = true" +
            " AND (p.executionStatus = 'WAITING' OR p.executionStatus = 'FAILED')")
    List<Payment> findByAccountId(@Param("userId") Long userId, Pageable pageable);

    List<Payment> findByOrderId(@Param("orderId") String orderId);

    Payment findByPaymentIntentId(@Param("paymentIntentId") String paymentIntentId);
}
