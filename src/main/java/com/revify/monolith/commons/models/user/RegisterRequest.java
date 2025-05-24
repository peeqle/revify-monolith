package com.revify.monolith.commons.models.user;

import com.revify.monolith.commons.geolocation.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private String residence;
}
