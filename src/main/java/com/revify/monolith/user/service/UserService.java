package com.revify.monolith.user.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.exceptions.LockedUserOperationsException;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.exceptions.UserPersistenceException;
import com.revify.monolith.commons.exceptions.UserSessionException;
import com.revify.monolith.commons.models.auth.AccountActivationResponse;
import com.revify.monolith.commons.models.auth.Response;
import com.revify.monolith.commons.models.user.UpdateAccountRequest;
import com.revify.monolith.commons.models.user.UpdateUserResponse;
import com.revify.monolith.keycloak.KeycloakService;
import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.revify.monolith.commons.ValidationContext.REGISTRATION_CODE_NOT_VALID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final WriteUserService writeUserService;

    private final ReadUserService readUserService;

    private final PhoneInteractionService phoneInteractionService;

    private final UserModificationService userModificationService;

    private final KeycloakService keycloakService;

    public boolean disableUser() throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserById(UserUtils.getUserId());
        if (appUser != null) {
            try {
                keycloakService.changeUserAvailability(appUser.getUsername(), false);

                appUser.setEnabled(false);
                writeUserService.saveUser(appUser);
            }catch (Exception e) {
                throw new UnauthorizedAccessError(e.getMessage());
            }
            return true;
        }

        throw new UserSessionException(String.format("Cannot find user %s", UserUtils.getUserId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addFavourite(Long id) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());

        if (readUserService.idExists(id)) {
            appUser.getFavourite().add(new AppUser(id));
        }

        writeUserService.saveUser(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeFavourite(Long id) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());
        Set<AppUser> favourite = appUser.getFavourite();
        appUser.setFavourite(favourite.stream().filter(e -> !Objects.equals(e.getId(), id)).collect(Collectors.toSet()));

        writeUserService.saveUser(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addBlocked(Long id) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());

        if (readUserService.idExists(id)) {
            appUser.getBlocked().add(new AppUser(id));
        }

        writeUserService.saveUser(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeBlocked(Long id) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());
        Set<AppUser> blocked = appUser.getBlocked();
        appUser.setBlocked(blocked.stream().filter(e -> !Objects.equals(e.getId(), id)).collect(Collectors.toSet()));

        writeUserService.saveUser(appUser);
    }

    public UpdateUserResponse updateUser(UpdateAccountRequest updateAccountRequest) throws UnauthorizedAccessError {
        AppUser appUser = readUserService.loadUserByUsername(UserUtils.getUsername());
        if (appUser == null) {
            throw new UserPersistenceException("Cannot find user for updateAccountRequest: " + updateAccountRequest.toString());
        }

        if (!appUser.isEnabled() || appUser.isLocked()) {
            throw new LockedUserOperationsException();
        }

        if (!appUser.getId().equals(updateAccountRequest.getUserId())) {
            throw new RuntimeException("User profile corrupted");
        }
        UpdateUserResponse updateUserResponse = new UpdateUserResponse();
        if (!Objects.equals(appUser.getEmail(), updateAccountRequest.getEmail())) {
            //send to user notification of change
            userModificationService.createNewEmailModificationRequest(updateAccountRequest.getEmail());
            updateUserResponse.setEmailChanged(true);
        }

        if (!Objects.equals(appUser.getPhoneNumber(), updateAccountRequest.getPhoneNumber())) {
            //send to user SMS with code for number change
            userModificationService.createNewPhoneModificationRequest(updateAccountRequest.getPhoneNumber());
            updateUserResponse.setPhoneChanged(true);
        }
        return updateUserResponse;
    }

    public Response checkCodeAndEnable(String code) {
        AppUser appUser = readUserService.loadUserById(UserUtils.getUserId());

        PhoneVerificationCode lastUserCode = phoneInteractionService.findLastUserCode(appUser);
        if (lastUserCode != null) {
            if (lastUserCode.getCode().equals(code)) {
                appUser.setEnabled(true);
                writeUserService.store(appUser);

                try {
                    keycloakService.changeUserAvailability(appUser.getUsername(), true);
                } catch (Exception e) {
                    log.error("Cannot update user after code being accepted.", e);
                    throw new RuntimeException(e);
                }
                phoneInteractionService.removeAllForUser(appUser);
                return Response.success();
            }
            return new Response(REGISTRATION_CODE_NOT_VALID);
        }
        phoneInteractionService.removeAllForUser(appUser);
        return new AccountActivationResponse(null, true);
    }
}
