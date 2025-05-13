package com.revify.monolith.commons.models.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemProcessing implements Serializable {
    private String itemId;
    private List<String> itemUrl;
}
