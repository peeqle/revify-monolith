package com.revify.monolith.finance.model;

import com.revify.monolith.commons.finance.InsuranceCertificate;
import com.revify.monolith.commons.finance.Price;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "item_insurance")

@AllArgsConstructor
@NoArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String itemId;
    private Long userId;
    @Embedded
    private Price insurancePrice;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "item_price_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "item_price_amount"))
    })
    private Price itemPrice;

    private Boolean isActive = true;
    private Boolean isPayed = false;

    private InsuranceCertificate certificate;

    public Insurance(UUID id) {
        this.id = id;
    }
}
