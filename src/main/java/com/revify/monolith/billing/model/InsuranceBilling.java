package com.revify.monolith.billing.model;

import com.revify.monolith.billing.utils.BillingEntityListener;
import com.revify.monolith.finance.model.Insurance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "insurance_billing")
@EntityListeners(BillingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.JOINED)
public class InsuranceBilling extends Billing {

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Insurance insurance;
}
