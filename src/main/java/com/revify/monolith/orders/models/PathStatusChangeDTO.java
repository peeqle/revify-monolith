package com.revify.monolith.orders.models;

public record PathStatusChangeDTO(Boolean isAcceptedByCustomer, Boolean isArchived, Boolean isCompleted) {
}
