package com.revify.monolith.notifications.service.util;


import com.revify.monolith.notifications.domain.FirebaseToken;
import com.revify.monolith.notifications.domain.dto.FirebaseTokenDTO;

public class TokenMapper {
    public static FirebaseToken from(FirebaseTokenDTO firebaseTokenDTO) {
        return FirebaseToken.builder()
                .registrationId(firebaseTokenDTO.getRegistrationToken()).build();
    }
}
