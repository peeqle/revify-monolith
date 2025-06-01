package com.revify.monolith.shoplift.model;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private Double lat;
    private Double lon;

    private List<Category> categories;
    private String query;
    private Long minDuration;
    private Price maxEntryDeliveryPrice;
    private int offset;
    private Integer limit;
    private String timeSort;

    private Boolean isCourier;

    private Set<String> itemsFilter;

    public Integer getLimit() {
        if (this.limit == null || this.limit <= 0) {
            return 10;
        }
        return this.limit;
    }
}
