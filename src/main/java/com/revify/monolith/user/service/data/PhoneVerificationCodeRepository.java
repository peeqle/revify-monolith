package com.revify.monolith.user.service.data;

import com.revify.monolith.user.models.PhoneVerificationCode;
import com.revify.monolith.user.models.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneVerificationCodeRepository extends JpaRepository<PhoneVerificationCode, Long> {
    List<PhoneVerificationCode> findAllByAppUser(AppUser appUser);

    PhoneVerificationCode findPhoneVerificationCodeByPhoneNumber(String phoneNumber);

    PhoneVerificationCode findPhoneVerificationCodeByCode(String code);

    boolean existsByCode(String code);

    void deleteByAppUser(AppUser appUser);
}
