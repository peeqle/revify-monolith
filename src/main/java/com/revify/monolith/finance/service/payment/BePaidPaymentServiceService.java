package com.revify.monolith.finance.service.payment;

import com.revify.monolith.finance.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BePaidPaymentServiceService implements PaymentService {

    @Override
    public List<String> createBatchTransaction(List items) throws Exception {
        return List.of();
    }
}
