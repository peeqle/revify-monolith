package com.revify.monolith.commons.models.queue;

import com.revify.monolith.commons.messaging.KafkaTopic;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerCreationOptions {
    private String groupId;
    private String bootstrapAddress;

    private String key;
    private KafkaTopic topic;
    private String partitions;
}