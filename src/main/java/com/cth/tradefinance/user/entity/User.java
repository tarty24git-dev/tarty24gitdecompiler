package com.cth.tradefinance.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String email;

    private String mobileNumber;

    @Column(nullable = false, length = 20)
    private String role; // ROLE_ADMIN, ROLE_USER

    @Builder.Default
    private boolean accountLocked = false;

    @Builder.Default
    private boolean mfaEnabled = false;

    private String otpCode;

    private LocalDateTime otpExpiry;

    private LocalDateTime passwordExpiryDate;

    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (passwordExpiryDate == null) {
            passwordExpiryDate = LocalDateTime.now().plusDays(90);
        }
    }
}
