package com.revify.monolith.resource.data.models;

import com.revify.monolith.commons.models.ResourceEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import static com.revify.monolith.commons.models.ResourceEntityType.ITEM;


@Data
@Builder

@AllArgsConstructor
@NoArgsConstructor
public class FileOptions implements Serializable {
    @Serial
    private static final long serialVersionUID = 12231L;

    private String entityId;
    private Long order;

    private ResourceEntityType entityType = ITEM;
}
