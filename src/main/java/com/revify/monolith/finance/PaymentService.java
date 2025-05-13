package com.revify.monolith.finance;

import java.util.List;

public interface PaymentService<T> {
    List<String> createBatchTransaction(List<T> items) throws Exception;
}
