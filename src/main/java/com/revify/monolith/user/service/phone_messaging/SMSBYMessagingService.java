package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSBYMessagingService implements PhoneMessagingService {

    private final CodeGenerator codeGenerator;

    @Override
    public void sendMessage(String phoneNumber, String content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PhoneVerificationCode sendCodeVerification(AppUser appUser) {
        PhoneVerificationCode phoneVerificationCode = codeGenerator.generateCode(appUser, 0);
        sendMessage(appUser.getPhoneNumber(), MessageUtil.prepareCodeMessage(phoneVerificationCode.getCode()));
        return phoneVerificationCode;
    }
}
