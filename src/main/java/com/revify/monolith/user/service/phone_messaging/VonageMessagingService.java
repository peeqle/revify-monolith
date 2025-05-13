package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!dev")
@Service
@RequiredArgsConstructor
public class VonageMessagingService implements PhoneMessagingService {

    private final VonageClient vonageClient;

    private final CodeGenerator codeGenerator;

    @Override
    public void sendMessage(String phoneNumber, String content) {
        TextMessage message = new TextMessage("REVIFY", phoneNumber, content);

        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);

        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully to " + phoneNumber);
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }

    @Override
    public PhoneVerificationCode sendCodeVerification(AppUser appUser) {
        PhoneVerificationCode phoneVerificationCode = codeGenerator.generateCode(appUser, 0);
        sendMessage(appUser.getPhoneNumber(), MessageUtil.prepareCodeMessage(phoneVerificationCode.getCode()));
        return phoneVerificationCode;
    }
}
