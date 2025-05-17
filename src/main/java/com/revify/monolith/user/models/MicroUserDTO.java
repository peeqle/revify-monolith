package com.revify.monolith.user.models;

import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.AppUserOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicroUserDTO {
    private Long id;
    private String username;
    private String displayName;

    private AppUserOptions options;

    public static MicroUserDTO from(AppUser user) {
        return MicroUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getCommonUserName())
                .options(user.getAppUserOptions())
                .build();
    }
}
