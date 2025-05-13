package com.revify.monolith.finance.model.addons;

import lombok.Getter;

@Getter
public enum PaymentExecutionStatus {
    EXECUTED,
    PENDING,
    FAILED,
}
