package com.revify.monolith.user.resource;

import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.models.DTO.AppUserDTO;
import com.revify.monolith.user.service.UserModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class UserChangesActivationController {

    private final UserModificationService userModificationService;

    @PostMapping("/phone")
    public ResponseEntity<AppUserDTO> activateMobile(@RequestParam("phoneCode") String phoneCode) throws UnauthorizedAccessError {
        return ResponseEntity.ok(AppUserDTO.from(userModificationService.activateChangedUserPhone(phoneCode)));
    }

    @PostMapping("/email")
    public ResponseEntity<AppUserDTO> activateEmail(@RequestParam("emailCode") String emailCode) throws UnauthorizedAccessError {
        return ResponseEntity.ok(AppUserDTO.from(userModificationService.activateChangedUserEmail(emailCode)));
    }
}
