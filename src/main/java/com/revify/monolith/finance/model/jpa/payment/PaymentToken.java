package com.revify.monolith.finance.model.jpa.payment;

import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "payment_token")
@NoArgsConstructor
@AllArgsConstructor
public class PaymentToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_token", nullable = false, unique = true)
    private String cardToken;

    @ManyToOne
    @JoinColumn(name = "payment_system_account_id", nullable = false)
    private PaymentSystemAccount paymentSystemAccount;

    @Enumerated(EnumType.STRING)
    private PaymentProcessor paymentProcessor;

    private Long tokenExpiration;
}
