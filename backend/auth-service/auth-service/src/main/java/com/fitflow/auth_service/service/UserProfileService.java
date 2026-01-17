package com.fitflow.auth_service.service;

import com.fitflow.auth_service.dto.ChangePasswordDTO;
import com.fitflow.auth_service.dto.UpdateProfileDTO;
import com.fitflow.auth_service.dto.UserProfileDTO;
import com.fitflow.auth_service.model.User;
import com.fitflow.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Pobieranie profilu użytkownika
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Użytkownik nie znaleziony"));

        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .preferredTrainingType(user.getPreferredTrainingType())
                .experienceLevel(user.getExperienceLevel())
                .trainingGoal(user.getTrainingGoal())
                .trainingDaysPerWeek(user.getTrainingDaysPerWeek())
                .notificationsEnabled(user.getNotificationsEnabled())
                .build();
    }

    // Aktualizowanie profilu użytkownika
    public UserProfileDTO updateProfile(Long userId, UpdateProfileDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Użytkownik nie znaleziony"));

        // Aktualizuj dane osobowe
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }

        // Aktualizuj preferencje treningowe
        if (updateDTO.getPreferredTrainingType() != null) {
            user.setPreferredTrainingType(updateDTO.getPreferredTrainingType());
        }
        if (updateDTO.getExperienceLevel() != null) {
            user.setExperienceLevel(updateDTO.getExperienceLevel());
        }
        if (updateDTO.getTrainingGoal() != null) {
            user.setTrainingGoal(updateDTO.getTrainingGoal());
        }
        if (updateDTO.getTrainingDaysPerWeek() != null) {
            user.setTrainingDaysPerWeek(updateDTO.getTrainingDaysPerWeek());
        }
        if (updateDTO.getNotificationsEnabled() != null) {
            user.setNotificationsEnabled(updateDTO.getNotificationsEnabled());
        }

        User savedUser = userRepository.save(user);

        return UserProfileDTO.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phoneNumber(savedUser.getPhoneNumber())
                .bio(savedUser.getBio())
                .preferredTrainingType(savedUser.getPreferredTrainingType())
                .experienceLevel(savedUser.getExperienceLevel())
                .trainingGoal(savedUser.getTrainingGoal())
                .trainingDaysPerWeek(savedUser.getTrainingDaysPerWeek())
                .notificationsEnabled(savedUser.getNotificationsEnabled())
                .build();
    }

    // Zmiana hasła
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Użytkownik nie znaleziony"));

        // Sprawdzenie czy hasła się zgadzają
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("Nowe hasło i potwierdzenie hasła się nie zgadzają!");
        }

        // Sprawdzenie czy obecne hasło jest prawidłowe
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Obecne hasło jest nieprawidłowe!");
        }

        // Zmiana hasła
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    // Pobieranie preferencji treningowych
    public UserProfileDTO getTrainingPreferences(Long userId) {
        return getProfile(userId);
    }

    // Usunięcie konta (soft delete - oznaczenie jako usunięte)
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Użytkownik nie znaleziony"));

        // Tu możemy dodać pola jak "deletedAt" lub "isActive"
        // Na razie usuwamy obraz
        userRepository.delete(user);
    }
}
