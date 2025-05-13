package com.revify.monolith.commons.exceptions;

public class UserCreationException extends IllegalStateException{
    public UserCreationException(String s) {
        super(s);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
