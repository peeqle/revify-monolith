package com.revify.monolith.shoplift.model.req;

import com.revify.monolith.geo.model.shared.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Create_Shoplift {
    private List<String> shopIds;

    private Destination destination;

    private String currency;
    private Double minEntryDeliveryPrice;
    private Integer maxEntries;

    private Long deliveryCutoffTime;
}
