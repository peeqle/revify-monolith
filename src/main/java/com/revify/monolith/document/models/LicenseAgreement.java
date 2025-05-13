package com.revify.monolith.document.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "license_agreement", schema = "legal")
public class LicenseAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private LicenseModel license;

    private Long createdAt;
}
