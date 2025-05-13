package com.revify.monolith.finance.service.payment;


import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.PaymentProcessor;
import com.revify.monolith.finance.PaymentService;
import com.revify.monolith.finance.model.jpa.payment.Payment;
import com.revify.monolith.finance.service.utils.PriceUtils;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceService implements PaymentService<Payment> {

    @Override
    public List<String> createBatchTransaction(List<Payment> payments) throws Exception {
        List<String> transferIds = new ArrayList<>();

        for (Payment request : payments) {
            TransferCreateParams params = TransferCreateParams.builder()
                    .setAmount(PriceUtils.convertToCents(request.getPrice()))
                    .setCurrency(Currency.EURO.getName())
                    .setDestination(request.getAccount().getPaymentToken().stream()
                            .filter(e -> e.getPaymentProcessor().equals(PaymentProcessor.STRIPE))
                            .findFirst().orElseThrow().getCardToken())
                    .setDescription(request.getDescription())
                    .build();

            Transfer transfer = Transfer.create(params);
            transferIds.add(transfer.getId());
        }

        return transferIds;
    }
}
