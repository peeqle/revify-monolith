package com.revify.monolith.user.service.phone_messaging;

import com.revify.monolith.RateLimitService;
import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.data.PhoneVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhoneInteractionService {

    private final PhoneVerificationCodeRepository phoneVerificationCodeRepository;

    private final RegionalBeanResolver regionalBeanResolver;

    private final ReadUserService readUserService;

    private final RateLimitService rateLimitService;

    private static final int MAX_RESEND_ATTEMPTS = 3;
    private static final long RESEND_WINDOW_SECONDS = 300;


    public PhoneVerificationCode verifyPhone(AppUser appUser) {
        PhoneMessagingService phoneMessagingService = regionalBeanResolver.getPhoneMessagingService(appUser.getAppUserOptions().getResidence());
        return phoneMessagingService.sendCodeVerification(appUser);
    }

    public void retryCodeVerification(String phone) {
        AppUser appUser = readUserService.loadUserByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        String rateLimitKey = "resend_code:" + phone;
        if (rateLimitService.isRateLimited(rateLimitKey, MAX_RESEND_ATTEMPTS, RESEND_WINDOW_SECONDS)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many resend attempts. Please wait.");
        }

        rateLimitService.recordRequest(rateLimitKey);
        removeAllForUser(appUser);

        verifyPhone(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
