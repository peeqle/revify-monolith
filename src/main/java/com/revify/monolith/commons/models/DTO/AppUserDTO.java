package com.revify.monolith.commons.models.DTO;

import com.revify.monolith.user.models.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;

    private boolean overrideChange = false;

    public static AppUserDTO from(AppUser appUser) {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.id = appUser.getId();
        appUserDTO.username = appUser.getUsername();
        appUserDTO.email = appUser.getEmail();
        appUserDTO.phoneNumber = appUser.getPhoneNumber();
        return appUserDTO;
    }

    public boolean materialized() {
        return this.username != null && this.email != null && this.phoneNumber != null;
    }
}
