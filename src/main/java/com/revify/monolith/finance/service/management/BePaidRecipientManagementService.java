package com.revify.monolith.finance.service.management;

import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.finance.RecipientProcessor;
import com.stripe.model.Account;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BePaidRecipientManagementService implements RecipientProcessor<Account, Customer> {

    @Override
    public Account registerCustomer(RecipientCreation recipientCreation) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Customer registerCourier(RecipientCreation recipientCreation) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void attachPaymentMethod(String paymentMethodId, String accountId) throws Exception {

    }

    @Override
    public boolean paymentMethodIsValid(String paymentMethodId) {
        return false;
    }
}
