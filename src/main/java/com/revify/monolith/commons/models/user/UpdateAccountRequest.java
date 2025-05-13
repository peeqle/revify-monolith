package com.revify.monolith.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAccountRequest {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String username;
    private UserRole role;
}
