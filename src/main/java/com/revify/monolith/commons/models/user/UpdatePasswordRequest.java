package com.revify.monolith.commons.models.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
