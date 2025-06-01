package com.revify.monolith.finance.messaging;

import com.revify.monolith.config.messaging.RabbitMqEndpointsConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DelayProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPaymentExpirationMessage(String paymentId, long delayInMilliseconds) {
        String messagePayload = "{\"paymentId\": " + paymentId + "}";

        rabbitTemplate.convertAndSend(
                RabbitMqEndpointsConfiguration.DELAYED_EXCHANGE_NAME,
                RabbitMqEndpointsConfiguration.PAYMENT_ROUTING_KEY,
                messagePayload,
                message -> {
                    message.getMessageProperties().setDelayLong(delayInMilliseconds);
                    return message;
                }
        );
        log.debug("Sent delayed message for paymentId " + paymentId + " with delay " + delayInMilliseconds + "ms");
    }

    public void sendOrderSummarization(String orderId, long delayInMilliseconds) {
        String messagePayload = "{\"orderId\": " + orderId + "}";

        rabbitTemplate.convertAndSend(
                RabbitMqEndpointsConfiguration.DELAYED_EXCHANGE_NAME,
                RabbitMqEndpointsConfiguration.ORDERS_ROUTING_KEY,
                messagePayload,
                message -> {
                    message.getMessageProperties().setDelayLong(delayInMilliseconds);
                    return message;
                }
        );
        log.debug("Sent delayed message for orderId " + orderId + " with delay " + delayInMilliseconds + "ms");
    }
}
