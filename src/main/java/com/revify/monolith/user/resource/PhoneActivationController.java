package com.revify.monolith.user.resource;


import com.revify.monolith.commons.models.auth.Response;
import com.revify.monolith.user.service.UserService;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-activities")
@RequiredArgsConstructor
public class PhoneActivationController {
    private final UserService userService;

    private final PhoneInteractionService phoneInteractionService;

    @PostMapping("/phone-code-enable")
    public ResponseEntity<?> enableWithCode(@RequestParam("phone") String phone, @RequestParam("providedCode") String providedCode) {
        return ResponseEntity.ok(userService.checkCodeAndEnable(phone, providedCode));
    }

    @PostMapping("/phone-code-resend")
    public ResponseEntity<?> resendCode(@RequestParam("phone") String phone) {
        phoneInteractionService.retryCodeVerification(phone);
        return ResponseEntity.ok(Response.success());
    }
}
