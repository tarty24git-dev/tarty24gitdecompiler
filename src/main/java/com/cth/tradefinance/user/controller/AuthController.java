package com.cth.tradefinance.user.controller;

import com.cth.tradefinance.user.entity.User;
import com.cth.tradefinance.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/users")
    public String userManagementPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("mfaEnabled", userService.isMfaToggle());
        model.addAttribute("ldapEnabled", userService.isLdapToggle());
        return "users";
    }

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/api/users/{id}/lock")
    @ResponseBody
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "User locked successfully");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/api/users/{id}/unlock")
    @ResponseBody
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "User unlocked successfully");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/api/users/{id}/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("newPassword");
            userService.resetPassword(id, newPassword);
            Map<String, String> res = new HashMap<>();
            res.put("message", "Password reset successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/api/settings/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleSettings(@RequestBody Map<String, Boolean> settings) {
        if (settings.containsKey("mfa")) {
            userService.setMfaToggle(settings.get("mfa"));
        }
        if (settings.containsKey("ldap")) {
            userService.setLdapToggle(settings.get("ldap"));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("mfaEnabled", userService.isMfaToggle());
        res.put("ldapEnabled", userService.isLdapToggle());
        return ResponseEntity.ok(res);
    }
}
