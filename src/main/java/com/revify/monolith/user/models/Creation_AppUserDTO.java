package com.revify.monolith.user.models;

import com.revify.monolith.commons.models.DTO.AppUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Creation_AppUserDTO {
    private AppUserDTO createdUser;
    private Long phoneVerificationCodeExpirationTime;
    private Long phoneVerificationCodeId;
}
