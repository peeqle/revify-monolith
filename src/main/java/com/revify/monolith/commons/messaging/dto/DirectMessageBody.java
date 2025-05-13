package com.revify.monolith.commons.messaging.dto;

import com.revify.monolith.commons.messaging.DirectNotificationTopic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageBody extends MessageBody {
    private Long receiverId;
    private Long senderId;
    private String email;

    private DirectNotificationTopic directNotificationTopic = DirectNotificationTopic.BID;
}

