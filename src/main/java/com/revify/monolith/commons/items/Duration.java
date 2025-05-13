package com.revify.monolith.commons.items;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public enum Duration {
    DAYS_7,
    DAYS_14,
    DAYS_30;


    public static Instant getDuration(final Duration duration) {
        return switch (duration) {
            case DAYS_7 -> Instant.ofEpochMilli(7L * 24 * 60 * 60 * 1000);
            case DAYS_14 -> Instant.ofEpochMilli(14L * 24 * 60 * 60 * 1000);
            case DAYS_30 -> Instant.ofEpochMilli(30L * 24 * 60 * 60 * 1000);
        };
    }
}
