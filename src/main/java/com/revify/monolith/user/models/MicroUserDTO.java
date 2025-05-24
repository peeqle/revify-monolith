package com.revify.monolith.user.models;

import com.revify.monolith.commons.models.user.UserRole;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.AppUserOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MicroUserDTO {
    private Long id;
    private String username;
    private String displayName;
    private UserRole userRole;

    private MicroUserOptionsDTO options;

    public static MicroUserDTO from(AppUser user) {
        return MicroUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getClientUserRole())
                .displayName(user.getCommonUserName())
                .options(MicroUserOptionsDTO.from(user.getAppUserOptions()))
                .build();
    }
}
