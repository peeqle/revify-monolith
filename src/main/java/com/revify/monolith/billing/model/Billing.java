package com.revify.monolith.billing.model;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.finance.TaxRegion;
import com.revify.monolith.commons.messaging.dto.BillingCreation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@MappedSuperclass
public class Billing {

    @Version
    private Integer version;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Embedded
    private Price price;

    @Column(name = "billing_payed", nullable = false)
    private Boolean payed = Boolean.FALSE;

    private BillingCreation.BillingStrategy billingStrategy;

    @Enumerated(EnumType.STRING)
    private TaxRegion taxRegion;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;
}
