package com.revify.monolith.user.service;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class ServiceTask {
    private Integer retryCount = 5;
    private final Long createdAt = Instant.now().toEpochMilli();
}
