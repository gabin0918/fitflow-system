package com.fitflow.auth_service.controller;

import com.fitflow.auth_service.dto.ChangePasswordDTO;
import com.fitflow.auth_service.dto.UpdateProfileDTO;
import com.fitflow.auth_service.dto.UserProfileDTO;
import com.fitflow.auth_service.security.JwtUtils;
import com.fitflow.auth_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final JwtUtils jwtUtils;

    // Pobieranie profilu użytkownika
    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer "
            Long userId = jwtUtils.extractUserId(jwt);
            UserProfileDTO profile = userProfileService.getProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Aktualizowanie profilu
    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileDTO updateDTO) {
        try {
            String jwt = token.substring(7);
            Long userId = jwtUtils.extractUserId(jwt);
            UserProfileDTO updatedProfile = userProfileService.updateProfile(userId, updateDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Zmiana hasła
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            String jwt = token.substring(7);
            Long userId = jwtUtils.extractUserId(jwt);
            userProfileService.changePassword(userId, changePasswordDTO);
            return ResponseEntity.ok("Hasło zmienione pomyślnie!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd podczas zmiany hasła");
        }
    }

    // Pobieranie preferencji treningowych
    @GetMapping("/training-preferences")
    public ResponseEntity<UserProfileDTO> getTrainingPreferences(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            Long userId = jwtUtils.extractUserId(jwt);
            UserProfileDTO preferences = userProfileService.getTrainingPreferences(userId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Usunięcie konta
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            Long userId = jwtUtils.extractUserId(jwt);
            userProfileService.deleteAccount(userId);
            return ResponseEntity.ok("Konto usunięte pomyślnie!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd podczas usuwania konta");
        }
    }
}
