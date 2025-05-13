package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.commons.exceptions.UsernameNotFoundException;
import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.data.PhoneVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhoneInteractionService {

    private final PhoneVerificationCodeRepository phoneVerificationCodeRepository;

    private final RegionalBeanResolver regionalBeanResolver;

    private final ReadUserService readUserService;

    public PhoneVerificationCode verifyPhone(AppUser appUser) {
        PhoneMessagingService phoneMessagingService = regionalBeanResolver.getPhoneMessagingService(appUser.getAppUserOptions().getResidence());
        return phoneMessagingService.sendCodeVerification(appUser);
    }

    public PhoneVerificationCode retryCodeVerification() {
        AppUser appUser = readUserService.getCurrentUser().orElseThrow(UsernameNotFoundException::new);
        PhoneVerificationCode lastUserCode = findLastUserCode(appUser);

        if (lastUserCode != null) {
            return lastUserCode;
        }

        removeAllForUser(appUser);
        return verifyPhone(appUser);
    }

    public void removeAllForUser(AppUser user) {
        phoneVerificationCodeRepository.deleteByAppUser(user);
    }

    public PhoneVerificationCode findLastUserCode(AppUser appUser) {
        List<PhoneVerificationCode> phoneVerificationCodes =
                phoneVerificationCodeRepository.findAllByAppUser(appUser);
        if (!phoneVerificationCodes.isEmpty()) {
            var verificationCode = phoneVerificationCodes.stream()
                    .filter(el -> el.getExpirationTime() > System.currentTimeMillis() + 30_000)
                    .max(Comparator.comparing(PhoneVerificationCode::getExpirationTime));

            if (verificationCode.isPresent()) {
                return verificationCode.get();
            }
        }
        return null;
    }
}
