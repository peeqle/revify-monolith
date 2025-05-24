package com.revify.monolith.finance;


import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;

public interface RecipientProcessor<Z,O> {

    Z registerCustomer(RecipientCreation recipientCreation) throws Exception;
    O registerCourier(RecipientCreation recipientCreation) throws Exception;

    void attachPaymentMethod(String paymentMethodId, String accountId) throws Exception;

    boolean paymentMethodIsValid(String paymentMethodId);
}
