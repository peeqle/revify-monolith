package com.revify.monolith.finance.model;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.finance.model.enums.OperationType;
import com.revify.monolith.user.models.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "operation", schema = "system")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser user;
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Embedded
    @Column(nullable = false)
    private Price amount;

    private Long createdAt;
    private Long executionDate;
}
