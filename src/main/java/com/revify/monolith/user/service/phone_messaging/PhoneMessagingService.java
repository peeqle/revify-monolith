package com.revify.monolith.user.service.phone_messaging;


import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;

public interface PhoneMessagingService {

    void sendMessage(String phoneNumber, String message);

    PhoneVerificationCode sendCodeVerification(AppUser appUser);
}
