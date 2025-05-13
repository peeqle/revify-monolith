package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("dev")
@Service
@RequiredArgsConstructor
public class MockedMessagingService implements PhoneMessagingService {

    private final CodeGenerator codeGenerator;

    @Override
    public void sendMessage(String phoneNumber, String content) {
        log.info("**Sent message to the client code: 111-111");
    }

    @Override
    public PhoneVerificationCode sendCodeVerification(AppUser appUser) {
        PhoneVerificationCode phoneVerificationCode = codeGenerator.generateCode(appUser, 0);
        sendMessage(appUser.getPhoneNumber(), MessageUtil.prepareCodeMessage(phoneVerificationCode.getCode()));
        return phoneVerificationCode;
    }
}
