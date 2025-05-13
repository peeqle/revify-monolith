package com.revify.monolith.commons.exceptions;

public class DEVException extends RuntimeException {
    public DEVException() {
        super("Method is in development");
    }
}
