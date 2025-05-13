package com.revify.monolith.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private String email;
    private String phoneNumber;
    private Boolean emailChanged = false;
    private Boolean phoneChanged = false;
}
