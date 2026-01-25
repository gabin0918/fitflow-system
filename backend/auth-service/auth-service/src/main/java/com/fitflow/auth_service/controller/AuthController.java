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

    // 1. REJESTRACJA
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO dto) {
        try {
            authService.registerUser(dto);
            return ResponseEntity.ok("Użytkownik zarejestrowany pomyślnie!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. LOGOWANIE
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto) {
        try {
            String token = authService.login(dto);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 3. POBIERANIE UŻYTKOWNIKA PO ID (Wymagane przez Gym-Operations)
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            User user = authService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 4. POBIERANIE LISTY TRENERÓW (Do żółtego panelu w React)
    @GetMapping("/trainers")
    public ResponseEntity<List<TrainerDTO>> getAvailableTrainers() {
        try {
            List<TrainerDTO> trainers = authService.getTrainersList();
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}