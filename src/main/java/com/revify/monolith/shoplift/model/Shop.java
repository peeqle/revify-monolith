package com.revify.monolith.shoplift.model;

import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.commons.items.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;
    @NotNull
    @Column(unique = true)
    private String URL;

    @ElementCollection
    private Set<CountryCode> countries;

    @ElementCollection
    private Set<Category> categories;

    private Double relevance = 1.0;

    private Long lastMonthDeliveries = 0L;
    private Long currentMonthDeliveries = 0L;

    private Double avgPriceDiff = 1.0;
}
