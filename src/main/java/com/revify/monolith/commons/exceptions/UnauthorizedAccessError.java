package com.revify.monolith.commons.exceptions;

public class UnauthorizedAccessError extends RuntimeException {
    public UnauthorizedAccessError(String message) {
        super(message);
    }

    public UnauthorizedAccessError() {
        super();
    }
}
