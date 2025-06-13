package com.revify.monolith.finance.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.revify.monolith.config.messaging.RabbitMqEndpointsConfiguration;
import com.revify.monolith.finance.model.addons.PaymentExecutionStatus;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.payment.Payment;
import com.revify.monolith.finance.service.repository.PaymentRepository;
import com.revify.monolith.orders.models.Order;
import com.revify.monolith.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventsHandler {
    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    private final Gson gson = new GsonBuilder().create();

    @Transactional
    @RabbitListener(queues = RabbitMqEndpointsConfiguration.PAYMENT_EXPIRATION)
    public void handlePaymentExpiration(String messagePayload, Message message) {
        Long receivedDelay = message.getMessageProperties().getReceivedDelayLong();
        log.debug("Received expired payment message: " + messagePayload);
        log.debug("Original delay: " + receivedDelay + "ms");
        log.debug("Received at: " + Instant.now());

        TypeToken<Map<String, String>> type = new TypeToken<>() {
        };
        Map<String, String> stringStringMap = gson.fromJson(messagePayload, type);
        if (stringStringMap != null && stringStringMap.containsKey("paymentId")) {
            try {
                Payment payment = paymentRepository.getReferenceById(UUID.fromString(stringStringMap.get("paymentId")));
                if (!payment.getExecutionStatus().equals(PaymentExecutionStatus.EXECUTED)) {
                    PaymentSystemAccount account = payment.getAccount();

                    Order order = orderService.findOrderById(payment.getOrderId());
                    order.setItems(order.getItems().stream().filter(e -> payment.getItems().contains(e)).collect(Collectors.toSet()));
                    order.setReceivers(order.getReceivers().stream().filter(e -> Objects.equals(account.getSystemUserId(), e)).collect(Collectors.toSet()));

                }
            } catch (EntityNotFoundException e) {
                log.error("Payment with id " + stringStringMap.get("paymentId") + " not found");
            }

        }
    }
}
