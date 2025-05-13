package com.revify.monolith.finance.service.repository;

import com.revify.monolith.finance.model.jpa.StripePaymentSystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripePaymentSystemRepository extends JpaRepository<StripePaymentSystemAccount, Long> {

}
