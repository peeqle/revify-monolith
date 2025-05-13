package com.revify.monolith.orders.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.messaging.ConsumerGroups;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderStatusUpdateRequest;
import com.revify.monolith.orders.models.Order;
import com.revify.monolith.orders.service.OrderWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderWriteService orderWriteService;
    private final Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = KafkaTopic.ORDER_MODEL_CREATION, groupId = ConsumerGroups.ORDERS)
    public void listenOrderCreation(@Payload String message) {
        logReceivedMessage(KafkaTopic.ORDER_MODEL_CREATION, message);
        processMessage(
                message,
                OrderCreationDTO.class,
                orderWriteService::createOrder,
                "Order created successfully"
        );
    }

    @KafkaListener(topics = KafkaTopic.ORDER_STATUS_UPDATE, groupId = ConsumerGroups.ORDERS)
    public void listenOrderStatusUpdate(@Payload String message) {
        logReceivedMessage(KafkaTopic.ORDER_STATUS_UPDATE, message);
        processMessage(
                message,
                OrderStatusUpdateRequest.class,
                orderWriteService::updateOrderStatus,
                "Order status updated successfully"
        );
    }

    private <T> void processMessage(String message, Class<T> dtoClass, MessageProcessor<T> processor, String successMessage) {
        try {
            T dto = gson.fromJson(message, dtoClass);
            processor.process(dto)
                    .subscribe(
                            order -> log.info("{}: {}", successMessage, order.getId()),
                            ex -> log.error("Failed to process message", ex),
                            () -> log.info("Processing completed for {}", dto.getClass().getSimpleName())
                    );
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    private void logReceivedMessage(String topic, String message) {
        log.info("[KAFKA] Received message from topic '{}': {}", topic, message);
    }

    @FunctionalInterface
    private interface MessageProcessor<T> {
        Mono<Order> process(T dto);
    }
}
