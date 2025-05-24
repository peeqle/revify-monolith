package com.revify.monolith.finance.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import static com.revify.monolith.commons.messaging.KafkaTopic.RECIPIENT_CREATION;


@Slf4j
@Component
@RequiredArgsConstructor
public class RecipientConsumer {

    private final RecipientService recipientService;

    private final Gson gson = new GsonBuilder().create();

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 10000, multiplier = 2.0)
    )
    @KafkaListener(topics = RECIPIENT_CREATION, groupId = ConsumerGroups.RECIPIENTS, containerFactory = "recipientKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> record) throws Exception {
        try {
            RecipientCreation recipientCreation = gson.fromJson(record.value(), RecipientCreation.class);
            log.info("Received record: {}", record);
            recipientService.registerRecipient(recipientCreation);
        } catch (Exception e) {
            log.warn("Cannot process recipient creation request {}", e);
            throw e;
        }
    }
}
