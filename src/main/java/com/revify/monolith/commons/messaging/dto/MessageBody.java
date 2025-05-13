package com.revify.monolith.commons.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode

@AllArgsConstructor
@NoArgsConstructor
public class MessageBody implements Serializable {
    private Long timestamp;
    private String title;
    private String body;
}
