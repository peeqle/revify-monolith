package com.revify.monolith.billing.messaging;


import com.revify.monolith.billing.service.BillingService;
import com.revify.monolith.billing.exception.InsurancePersistenceException;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.messaging.dto.BillingCreation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component("BillingOperationsConsumer")
@RequiredArgsConstructor
public class BillingConsumer {

    private final BillingService billingService;

    //todo make partition dependent and send traffic for highest precedence(refunds) to the dedicated partitions
    @KafkaListener(topics = KafkaTopic.BILLING_CREATION, containerFactory = "billingKafkaListenerContainerFactory")
    public void create(ConsumerRecord<String, BillingCreation> record) {
        try {
            log.debug("Received billing: {}", record.value());
            billingService.createBilling(record.value());
        } catch (InsurancePersistenceException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Failed to process record: {}", record.value(), e);
        }
    }
}
