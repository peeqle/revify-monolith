package com.revify.monolith.billing.model;

import com.revify.monolith.billing.utils.BillingEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_premium_billing")
@EntityListeners(BillingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.JOINED)
public class ItemPremiumBilling extends Billing {

    @Version
    private Integer version;

    @Column(name = "item_premium_id", nullable = false)
    private String itemPremiumId;
}
