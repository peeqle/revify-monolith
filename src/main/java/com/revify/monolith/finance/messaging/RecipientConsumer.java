package com.revify.monolith.finance.messaging;

import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.revify.monolith.commons.messaging.KafkaTopic.RECIPIENT_CREATION;


@Slf4j
@Component
@RequiredArgsConstructor
public class RecipientConsumer {

    private final RecipientService recipientService;

    @KafkaListener(topics = RECIPIENT_CREATION, groupId = ConsumerGroups.RECIPIENTS, containerFactory = "recipientKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, RecipientCreation> record, Acknowledgment acknowledgment) {
        try {
            log.info("Received record: {}", record);
            recipientService.registerRecipient(record.value());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.warn("Cannot process recipient creation request {}", e);
        }
    }
}
