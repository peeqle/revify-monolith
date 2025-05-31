package com.revify.monolith.finance.service.repository;

import com.revify.monolith.finance.model.jpa.payment.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT Payment FROM Payment  p where p.account.systemUserId = :userId" +
            " AND p.account.isActive = true AND (p.executionStatus = 'WAITING' OR p.executionStatus = 'FAILED')")
    List<Payment> findByAccountId(Long userId, Pageable pageable);
}
