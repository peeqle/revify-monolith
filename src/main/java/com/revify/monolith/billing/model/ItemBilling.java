package com.revify.monolith.billing.model;

import com.revify.monolith.billing.utils.BillingEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item_billing")
@EntityListeners(BillingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.JOINED)
public class ItemBilling extends Billing {

    @Version
    private Integer version;

    @Column(name = "item_id", nullable = false)
    private String itemId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ItemBilling parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ItemBilling> items;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<InsuranceBilling> insurance;
}
