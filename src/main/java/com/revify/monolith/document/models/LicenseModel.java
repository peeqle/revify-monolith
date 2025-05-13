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
@Table(name = "license", schema = "legal")
public class LicenseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "license_content", nullable = false)
    private String license;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "hash", nullable = false)
    private String hash;

    private Long createdAt;

    public LicenseModel(Long id) {
        this.id = id;
    }
}
