package com.revify.monolith.notifications;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.notifications.domain.FirebaseToken;
import com.revify.monolith.notifications.domain.dto.FirebaseTokenDTO;
import com.revify.monolith.notifications.service.fcm.FcmTokenService;
import com.revify.monolith.notifications.service.util.TokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.security.PermitAll;

@PermitAll
@RestController("/fcm")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class FcmController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/save-token")
    public Mono<ResponseEntity<FirebaseToken>> saveToken(FirebaseTokenDTO tokenDTO) {
        if (tokenDTO == null) {
            throw new RuntimeException("Token for cannot be NULL");
        }
        FirebaseToken firebaseToken = TokenMapper.from(tokenDTO);

        firebaseToken.setUserId(UserUtils.getUserId());
        return fcmTokenService.saveToken(firebaseToken)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
