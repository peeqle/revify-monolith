package com.revify.monolith.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StompAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(jwt.getSubject(), null,
                                    extractAuthorities(jwt));
                    accessor.setUser(authentication);
                } catch (JwtException e) {
                    throw new IllegalArgumentException("Invalid JWT token", e);
                }
            } else {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        }

        return message;
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> credentials = jwt.getClaims();
        if (credentials != null && credentials.containsKey("realm_access")) {
            Map<String, Object> realm = (Map<String, Object>) credentials.get("resource_access");
            Map<String, Object> resourceRoles = (Map<String, Object>) realm.get("revify-client");
            List<String> roles = (List<String>) resourceRoles.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
