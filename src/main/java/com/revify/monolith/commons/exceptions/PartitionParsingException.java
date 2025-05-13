package com.revify.monolith.commons.exceptions;

public class PartitionParsingException extends IllegalArgumentException {
    public PartitionParsingException(Class<?> clazz) {
        super("Cannot parse partition statement for " + clazz.getName());
    }
}
