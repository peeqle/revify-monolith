package com.revify.monolith.user.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.data.AppUserReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadUserService extends CrudService<AppUser> {

    private final AppUserReadRepository repository;

    public Optional<AppUser> getCurrentUser() {
        return repository.findById(UserUtils.getUserId());
    }

    public Boolean isUserActivated(String username) {
        return repository.isUserActivated(username);
    }

    public AppUser loadUserByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            Optional<AppUser> appUserByUsername = repository.findAppUserByUsername(username);
            if (appUserByUsername.isPresent()) {
                return appUserByUsername.get();
            }
        }
        throw new UsernameNotFoundException("Cannot find user with username: " + username);
    }

    public AppUser loadUserById(Long id) {
        if (id != null) {
            Optional<AppUser> appUserByUsername = repository.findById(id);
            if (appUserByUsername.isPresent()) {
                return appUserByUsername.get();
            }
        }
        throw new UsernameNotFoundException("Cannot find user with id: " + id);
    }

    public List<AppUser> loadUsersByUsernamePattern(String usernamePattern) {
        if (usernamePattern != null && !usernamePattern.isEmpty()) {
            return repository.findAllByUsernameIsLikeIgnoreCase(usernamePattern);
        }
        throw new UsernameNotFoundException("cannot find user with username pattern: " + usernamePattern);
    }

    public List<String> findAllBlockedBy(String keycloakUserId) {
        return repository.findAllBlockedBy(keycloakUserId);
    }

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public boolean phoneExists(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }

    public boolean usernameExists(String username) {
        return repository.existsByUsername(username);
    }

    public boolean idExists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public JpaRepository<AppUser, Long> getRepository() {
        return repository;
    }
}
