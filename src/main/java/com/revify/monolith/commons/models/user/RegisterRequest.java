package com.revify.monolith.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private Long userId;
    private String userRole;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private String residence;
    private String country;
    private String city;
    private String street;
    private String region;
    private String postalCode;
    private String apartmentHouse;
    private Integer day;
    private Integer month;
    private Integer year;

    private String ip;
    private String browserAccess;
}
