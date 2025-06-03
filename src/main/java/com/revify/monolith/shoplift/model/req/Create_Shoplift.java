package com.revify.monolith.shoplift.model.req;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.geo.model.shared.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Create_Shoplift {
    private String title;
    private String description;

    private List<String> shopIds;

    private Destination destination;
    private Price minEntryDeliveryPrice;
    private Integer maxEntries;

    private Long deliveryCutoffTime;

    private Boolean isRecurrent;
    private Boolean allowedSystemAppend;
}
