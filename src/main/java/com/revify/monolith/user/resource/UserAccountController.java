package com.revify.monolith.user.resource;


import com.revify.monolith.commons.ValidationContext;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.exceptions.UserCreationException;
import com.revify.monolith.commons.models.DTO.AppUserDTO;
import com.revify.monolith.commons.models.auth.Response;
import com.revify.monolith.commons.models.user.RegisterRequest;
import com.revify.monolith.commons.models.user.UpdateAccountRequest;
import com.revify.monolith.user.kafka.KafkaIntegrationService;
import com.revify.monolith.user.models.Creation_AppUserDTO;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.UserService;
import com.revify.monolith.user.service.WriteUserService;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import com.revify.monolith.user.service.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    private final WriteUserService writeUserService;

    private final KafkaIntegrationService kafkaIntegrationService;

    private final PhoneInteractionService phoneInteractionService;

    @PostMapping("/create")
    public ResponseEntity<?> createUserAccount(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest != null) {
            try {
                AppUser appUser = writeUserService.tryCreateUser(registerRequest);

//                PhoneVerificationCode phoneVerificationCode = phoneInteractionService.verifyPhone(appUser);
                kafkaIntegrationService.sendKafkaUserContext(appUser.getCommonUserInfo());
                kafkaIntegrationService.createChatUser(appUser, registerRequest.getPassword());

                return ResponseEntity.ok(appUser);
            } catch (UserCreationException e) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User cannot be created");
            } catch (RuntimeException e) {
                log.warn("User creation exception: {}", registerRequest.getUsername(), e);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUserInformation(UpdateAccountRequest updateAccountRequest) throws UnauthorizedAccessError {
        List<ValidationContext> validationContexts = RequestValidator.validateUpdateRequest(updateAccountRequest);
        if (validationContexts.isEmpty()) {
            return ResponseEntity.ok(userService.updateUser(updateAccountRequest));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.builder().validationContext(validationContexts).build());
    }


    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteSelfAccount() throws UnauthorizedAccessError {
        if (userService.disableUser()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
