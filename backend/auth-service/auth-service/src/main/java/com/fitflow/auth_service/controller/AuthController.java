package com.fitflow.auth_service.controller;

import com.fitflow.auth_service.dto.LoginRequestDTO;
import com.fitflow.auth_service.dto.UserRegistrationDTO;
import com.fitflow.auth_service.dto.TrainerDTO;
import com.fitflow.auth_service.service.AuthService;
import com.fitflow.auth_service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Rejestracja
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO dto) {
        try {
            authService.registerUser(dto);
            return ResponseEntity.ok("Użytkownik zarejestrowany pomyślnie!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Logowanie (NOWE)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto) {
        try {
            String token = authService.login(dto);
            return ResponseEntity.ok(token); // Zwracamy token JWT
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Pobieranie szczegółów użytkownika po ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = authService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Pobieranie listy dostępnych trenerów
    @GetMapping("/trainers")
    public ResponseEntity<?> getAvailableTrainers() {
        try {
            List<TrainerDTO> trainers = authService.getTrainersList();
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd podczas pobierania listy trenerów");
        }
    }
}