package com.revify.monolith.finance.model.addons;

import lombok.Getter;

@Getter
public enum PaymentExecutionStatus {
    WAITING(0),
    PENDING(1),
    EXECUTED(2),
    FAILED(3);

    private final int priority;

    PaymentExecutionStatus(int priority) {
        this.priority = priority;
    }

    public boolean isAfter(PaymentExecutionStatus other) {
        return this.priority > other.priority;
    }

    public boolean isBefore(PaymentExecutionStatus other) {
        return this.priority < other.priority;
    }

    public int comparePriority(PaymentExecutionStatus other) {
        return Integer.compare(this.priority, other.priority);
    }
}
