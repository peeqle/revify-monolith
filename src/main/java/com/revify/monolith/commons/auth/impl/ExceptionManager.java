package com.revify.monolith.commons.auth.impl;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(UnauthorizedAccessError.class)
    public ResponseEntity<?> handleUnauthorizedAccess(UnauthorizedAccessError e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access", "message", e.getMessage()));
    }
}
