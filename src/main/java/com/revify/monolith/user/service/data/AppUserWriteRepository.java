package com.revify.monolith.user.service.data;

import com.revify.monolith.user.models.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserWriteRepository extends JpaRepository<AppUser, Long> {
    void deleteAllByEmailAndUsername(String email, String username);
}
