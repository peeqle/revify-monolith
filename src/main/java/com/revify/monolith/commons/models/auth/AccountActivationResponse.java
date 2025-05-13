package com.revify.monolith.commons.models.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountActivationResponse extends Response {
    private Long codeExpiresAt;
    private Boolean expired;

    public AccountActivationResponse(Long codeExpiresAt) {
        this.codeExpiresAt = codeExpiresAt;
    }
}
