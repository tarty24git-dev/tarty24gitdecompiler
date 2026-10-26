package com.cth.tradefinance.user;

import com.cth.tradefinance.user.entity.User;
import com.cth.tradefinance.user.repository.UserRepository;
import com.cth.tradefinance.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class SecurityServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userService.initDefaultAdminUser();
    }

    @Test
    void testDefaultAdminUserCreation() {
        assertTrue(userRepository.existsByUsername("admin"));
        User admin = userRepository.findByUsername("admin").orElseThrow();
        assertEquals("ROLE_ADMIN", admin.getRole());
        assertFalse(admin.isAccountLocked());
    }

    @Test
    void testCreateUserValidationAndLocking() {
        User newUser = User.builder()
                .username("john_doe")
                .password("SecurePass123!")
                .fullName("John Doe")
                .email("john@cth.com")
                .build();

        User created = userService.createUser(newUser);
        assertNotNull(created.getId());

        userService.lockUser(created.getId());
        User lockedUser = userRepository.findById(created.getId()).orElseThrow();
        assertTrue(lockedUser.isAccountLocked());

        userService.unlockUser(created.getId());
        User unlockedUser = userRepository.findById(created.getId()).orElseThrow();
        assertFalse(unlockedUser.isAccountLocked());
    }

    @Test
    void testPasswordComplexityValidation() {
        assertThrows(IllegalArgumentException.class, () -> userService.validatePasswordComplexity("weak"));
        assertThrows(IllegalArgumentException.class, () -> userService.validatePasswordComplexity("NoSpecialChar123"));
        assertDoesNotThrow(() -> userService.validatePasswordComplexity("ValidPass123$"));
    }
}
