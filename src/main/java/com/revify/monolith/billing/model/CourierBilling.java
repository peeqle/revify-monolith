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
@Table(name = "courier_billing")
@EntityListeners(BillingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CourierBilling extends Billing{

    @Version
    private Integer version;

    @Column(name = "courier_id", nullable = false)
    private String courierId;
}
