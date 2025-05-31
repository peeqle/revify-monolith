package com.revify.monolith.shoplift.model.req;

import com.revify.monolith.geo.model.shared.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Create_Shoplift {
    private String shopId;

    private Destination destination;

    private String currency;
    private Double minEntryDeliveryPrice;
    private Integer maxEntries;

    private Long deliveryCutoffTime;
}
