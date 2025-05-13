package com.revify.monolith.user.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.modification.AppUserEmailModification;
import com.revify.monolith.user.models.user.modification.AppUserPhoneModification;
import com.revify.monolith.user.service.data.AppUserModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.revify.monolith.user.service.util.UserMailKeyGenerator.generateKey;


@Service
@RequiredArgsConstructor
public class UserModificationService {

    private final ReadUserService readUserService;

    private final WriteUserService writeUserService;

    private final AppUserModificationRepository appUserModificationRepository;

    public AppUserEmailModification createNewEmailModificationRequest(String email) {
        AppUserEmailModification emailModification = new AppUserEmailModification();
        emailModification.setEmail(email);
        emailModification.setEmailAcceptanceKey(generateKey());

        return appUserModificationRepository.save(emailModification);
    }

    public AppUserPhoneModification createNewPhoneModificationRequest(String phone) {
        AppUserPhoneModification phoneModification = new AppUserPhoneModification();
        phoneModification.setPhone(phone);
        phoneModification.setPhoneAcceptanceKey(generateKey());

        return appUserModificationRepository.save(phoneModification);
    }

    public AppUser activateChangedUserPhone(String phoneCode) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());

        AppUserPhoneModification appUserPhoneModification = appUserModificationRepository.findLastPhoneModificationByAppUser(appUser);
        if (appUserPhoneModification != null) {
            if (appUserPhoneModification.getPhoneAcceptanceKey().equals(phoneCode)) {
                appUser.setPhoneNumber(appUserPhoneModification.getPhone());

                return writeUserService.saveUser(appUser);
            }
        }
        return appUser;
    }

    public AppUser activateChangedUserEmail(String emailKey) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());

        AppUserEmailModification appUserEmailModification = appUserModificationRepository.findLastEmailModificationByAppUser(appUser);
        if (appUserEmailModification != null) {
            if (appUserEmailModification.getEmailAcceptanceKey().equals(emailKey)) {
                appUser.setPhoneNumber(appUserEmailModification.getEmail());

                return writeUserService.saveUser(appUser);
            }
        }
        return appUser;
    }
}
