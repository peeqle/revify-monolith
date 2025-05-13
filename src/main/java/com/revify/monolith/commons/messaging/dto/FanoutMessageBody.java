package com.revify.monolith.commons.messaging.dto;

import com.revify.monolith.commons.messaging.FanoutNotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FanoutMessageBody extends MessageBody {
    private Long ttl = Duration.ofHours(1).toMillis();
    private FanoutNotificationType notificationType = FanoutNotificationType.ALL;
}

