package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.data.PhoneVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final PhoneVerificationCodeRepository phoneVerificationCodeRepository;

    public PhoneVerificationCode generateCode(final AppUser appUser, int iteration) {
        if(iteration == 5) {
            throw new RuntimeException("Cannot generate code, iteration limit exceeded");
        }
        Random random = new Random();
        String code = String.valueOf(100000 + random.nextInt(900000));

        PhoneVerificationCode phoneVerificationCode = new PhoneVerificationCode();

        if (!phoneVerificationCodeRepository.existsByCode(code)) {
            phoneVerificationCode.setCode(code);
            phoneVerificationCode.setPhoneNumber(appUser.getPhoneNumber());
            phoneVerificationCode.setAppUser(appUser);
            phoneVerificationCode.setExpirationTime(System.currentTimeMillis() + 60 * 5 * 1000); // 5min
            phoneVerificationCode.setCreatedAt(System.currentTimeMillis());
            phoneVerificationCodeRepository.save(phoneVerificationCode);
        } else {
            generateCode(appUser, iteration + 1);
        }
        return phoneVerificationCode;
    }
}
