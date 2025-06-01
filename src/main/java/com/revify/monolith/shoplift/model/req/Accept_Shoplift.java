package com.revify.monolith.shoplift.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accept_Shoplift {
    private String shopliftId;
    private List<String> items;
    private Long estimatedDeliveryTime;
}
