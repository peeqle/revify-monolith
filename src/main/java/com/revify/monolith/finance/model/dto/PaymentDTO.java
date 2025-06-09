package com.revify.monolith.finance.model.dto;

import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.finance.model.addons.PaymentExecutionStatus;
import com.revify.monolith.finance.model.jpa.payment.Payment;

public record PaymentDTO(
        String id,
        Price price,
        String paymentIntentId,
        String paymentIntentClientSecret,
        String description,
        Boolean executedSuccessfully,
        PaymentExecutionStatus executionStatus,
        Long createdAt,
        Long executedAt,
        String orderId
) {
    public static PaymentDTO from(Payment payment) {
        return new PaymentDTO(payment.getId().toString(),
                payment.getPrice(),
                payment.getPaymentIntentId(),
                payment.getPaymentIntentClientSecret(),
                payment.getDescription(),
                payment.getExecutedSuccessfully(),
                payment.getExecutionStatus(),
                payment.getCreatedAt(),
                payment.getExecutedAt(),
                payment.getOrderId());
    }
}
