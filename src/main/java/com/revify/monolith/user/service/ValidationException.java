package com.revify.monolith.user.service;

import com.revify.monolith.commons.ValidationContext;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationContext> validationContexts;
    public ValidationException(List<ValidationContext> validationContext) {
        this.validationContexts = validationContext;
    }
}
