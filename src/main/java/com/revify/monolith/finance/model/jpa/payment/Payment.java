package com.revify.monolith.finance.model.jpa.payment;


import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.finance.model.addons.PaymentExecutionStatus;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {
    @Version
    private long version;

    @Serial
    private static final long serialVersionUID = 52L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private Price price;

    private String paymentIntentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private PaymentSystemAccount account;

    @NotNull
    @Max(value = 255, message = "Description cannot be longer than 255 chars")
    private String description;

    private Boolean executedSuccessfully;

    @Enumerated(EnumType.STRING)
    private PaymentExecutionStatus executionStatus;

    private Long createdAt;
    private Long executedAt;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    @ElementCollection
    private List<String> items;
}
