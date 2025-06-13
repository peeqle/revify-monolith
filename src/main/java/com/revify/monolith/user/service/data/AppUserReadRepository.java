package com.revify.monolith.user.service.data;

import com.revify.monolith.user.models.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserReadRepository extends JpaRepository<AppUser, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    @Query("select e.enabled from AppUser e where e.email=:email")
    boolean isUserActivated(String email);

    Optional<AppUser> findAppUserByUsername(String username);
    Optional<AppUser> findAppUserByEmail(String email);
    Optional<AppUser> findAppUserByPhoneNumber(String phone);

    List<AppUser> findAllByUsernameIsLikeIgnoreCase(String pattern);

    Optional<AppUser> findAppUserByUsernameAndPhoneNumberAndEmail(String username, String phoneNumber, String email);

    @Query(value = "select user_.keycloak_id from system.sys_app_user user_ where user_.id in " +
            "(select distinct blck.app_user_id from system.sys_app_user app_u " +
            "join system.sys_app_user_blocked blck on blck.blocked_id = app_u.id " +
            "where app_u.keycloak_id = :keycloakUserId)", nativeQuery = true)
    List<String> findAllBlockedBy(String keycloakUserId);

    @Query(value = "select user_.id from system.sys_app_user user_ where user_.id in " +
            "(select distinct blck.app_user_id from system.sys_app_user app_u " +
            "join system.sys_app_user_blocked blck on blck.blocked_id = app_u.id ", nativeQuery = true)
    List<Long> findAllBlockedBy(Long userId);
}
