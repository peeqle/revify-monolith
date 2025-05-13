package com.revify.monolith.user.service.data;

import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.modification.AppUserEmailModification;
import com.revify.monolith.user.models.user.modification.AppUserModification;
import com.revify.monolith.user.models.user.modification.AppUserPhoneModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserModificationRepository extends JpaRepository<AppUserModification, Long> {

    @Query("select e from AppUserPhoneModification e where e.appUser = :appUser order by e.createdAt desc limit 1")
    AppUserPhoneModification findLastPhoneModificationByAppUser(AppUser appUser);

    @Query("select e from AppUserEmailModification e where e.appUser = :appUser order by e.createdAt desc limit 1")
    AppUserEmailModification findLastEmailModificationByAppUser(AppUser appUser);
}
