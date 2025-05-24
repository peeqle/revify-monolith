package com.revify.monolith.commons.models.DTO;

import com.revify.monolith.user.models.MicroUserDTO;
import com.revify.monolith.user.models.MicroUserOptionsDTO;
import com.revify.monolith.user.models.user.AppUser;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO extends MicroUserDTO {
    private Long id;
    private String email;

    private boolean overrideChange = false;

    public static AppUserDTO from(AppUser appUser) {
        return AppUserDTO.builder()
                .displayName(appUser.getCommonUserName())
                .userRole(appUser.getClientUserRole())
                .options(MicroUserOptionsDTO.from(appUser.getAppUserOptions()))
                .email(appUser.getEmail())
                .id(appUser.getId())
                .build();
    }
}
