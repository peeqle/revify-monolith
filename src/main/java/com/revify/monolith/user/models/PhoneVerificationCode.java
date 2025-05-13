package com.revify.monolith.user.models;

import com.revify.monolith.user.models.user.AppUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "phone_verification_code")
public class PhoneVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String phoneNumber;
    @ManyToOne
    private AppUser appUser;
    private Long expirationTime;
    private Long createdAt;
}
