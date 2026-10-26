package com.cth.tradefinance.user.service;

import com.cth.tradefinance.user.entity.User;
import com.cth.tradefinance.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${cth.security.mfa.enabled:false}")
    private boolean mfaToggle;

    @Value("${cth.security.ldap.enabled:false}")
    private boolean ldapToggle;

    @PostConstruct
    public void initDefaultAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Qwerty123$"))
                    .fullName("Administrator")
                    .email("admin@cth.com")
                    .mobileNumber("+1234567890")
                    .role("ROLE_ADMIN")
                    .accountLocked(false)
                    .mfaEnabled(mfaToggle)
                    .passwordExpiryDate(LocalDateTime.now().plusDays(90))
                    .build();
            userRepository.save(admin);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        validatePasswordComplexity(user.getPassword());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    @Transactional
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setAccountLocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setAccountLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        validatePasswordComplexity(newPassword);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpiryDate(LocalDateTime.now().plusDays(90));
        userRepository.save(user);
    }

    public void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("Password must contain upper, lower, digit and special character.");
        }
    }

    public String generateOtp(User user) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        user.setOtpCode(code);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        return code;
    }

    public boolean isMfaToggle() {
        return mfaToggle;
    }

    public void setMfaToggle(boolean mfaToggle) {
        this.mfaToggle = mfaToggle;
    }

    public boolean isLdapToggle() {
        return ldapToggle;
    }

    public void setLdapToggle(boolean ldapToggle) {
        this.ldapToggle = ldapToggle;
    }
}
