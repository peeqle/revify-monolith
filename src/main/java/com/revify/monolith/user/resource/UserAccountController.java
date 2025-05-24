package com.revify.monolith.user.resource;


import com.revify.monolith.commons.ValidationContext;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.exceptions.UserCreationException;
import com.revify.monolith.commons.models.DTO.AppUserDTO;
import com.revify.monolith.commons.models.auth.Response;
import com.revify.monolith.commons.models.user.RegisterRequest;
import com.revify.monolith.commons.models.user.UpdateAccountRequest;
import com.revify.monolith.user.kafka.KafkaIntegrationService;
import com.revify.monolith.user.models.MicroUserDTO;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.UserService;
import com.revify.monolith.user.service.ValidationException;
import com.revify.monolith.user.service.WriteUserService;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import com.revify.monolith.user.service.util.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    private final WriteUserService writeUserService;
    private final ReadUserService readUserService;

    private final KafkaIntegrationService kafkaIntegrationService;

    private final PhoneInteractionService phoneInteractionService;

    @PostMapping("/create")
    public ResponseEntity<?> createUserAccount(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        if (registerRequest != null) {
            registerRequest.setIp(request.getRemoteAddr());
            registerRequest.setBrowserAccess(request.getHeader("User-Agent"));
            try {
                AppUser appUser = writeUserService.tryCreateUser(registerRequest);

//                PhoneVerificationCode phoneVerificationCode = phoneInteractionService.verifyPhone(appUser);
                kafkaIntegrationService.sendKafkaUserContext(appUser.getCommonUserInfo());
                kafkaIntegrationService.createChatUser(appUser, registerRequest.getPassword());

                return ResponseEntity.ok(AppUserDTO.from(appUser));
            } catch (UserCreationException e) {
                log.warn("User creation exception: {}", registerRequest.getUsername(), e);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User cannot be created");
            }catch (ValidationException e) {
                return ResponseEntity.badRequest().body(e.getValidationContexts());
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

    @GetMapping("/micro")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MicroUserDTO> microAccount(@RequestParam("userId") Long userId) {
        try {
            AppUser appUser = readUserService.loadUserById(userId);
            return ResponseEntity.ok(MicroUserDTO.from(appUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<AppUserDTO> getMe() {
        try {
            Optional<AppUser> appUser = readUserService.getCurrentUser();
            if (appUser.isPresent()) {
                return ResponseEntity.ok(AppUserDTO.from(appUser.get()));
            }
        } catch (Exception e) {
            log.warn("Error getting current user", e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
