package com.revify.monolith.config.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitMqEndpointsConfiguration {

    public static final String DELAYED_EXCHANGE_NAME = "delayed-payment-exchange";
    public static final String PAYMENT_EXPIRATION = "payment-expiration-queue";
    public static final String ORDER_SUMMARY = "order-summary-queue";
    public static final String PAYMENT_ROUTING_KEY = "payment.expire";
    public static final String ORDERS_ROUTING_KEY = "payment.expire";


    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue paymentExpirationQueue() {
        return new Queue(PAYMENT_EXPIRATION, true);
    }

    @Bean
    public Queue orderSummaryQueue() {
        return new Queue(ORDER_SUMMARY, true);
    }

    @Bean
    public Binding paymentExpirationBinding(Queue paymentExpirationQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(paymentExpirationQueue)
                .to(delayedExchange)
                .with(PAYMENT_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Binding orderExpirationBinding(Queue orderSummaryQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(orderSummaryQueue)
                .to(delayedExchange)
                .with(ORDERS_ROUTING_KEY)
                .noargs();
    }
}
