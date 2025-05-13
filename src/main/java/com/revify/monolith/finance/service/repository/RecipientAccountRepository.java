package com.revify.monolith.finance.service.repository;

import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.finance.model.jpa.payment.PaymentToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipientAccountRepository extends JpaRepository<PaymentToken, Long> {

    @Query("SELECT p FROM PaymentToken p JOIN p.paymentSystemAccount ps " +
            "WHERE ps.systemUserId = :accountUserId AND p.paymentProcessor = :paymentProcessor")
    List<PaymentToken> findAccountForPaymentMethod(@Param("accountUserId") Long accountUserId,
                                                   @Param("paymentProcessor") PaymentProcessor paymentProcessor);

    @Query("SELECT p FROM PaymentToken p JOIN p.paymentSystemAccount ps " +
            "WHERE ps.systemUserId = :accountUserId")
    Page<PaymentToken> findAllByAccountUserId(@Param("accountUserId") Long accountUserId, Pageable pageable);
}
