package com.revify.monolith.finance.model.dto;

import com.revify.monolith.commons.finance.PaymentProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientTokenDTO {
    private String paymentMethodId;
    private String recipientUserId;

    private PaymentProcessor paymentProcessor;
}
