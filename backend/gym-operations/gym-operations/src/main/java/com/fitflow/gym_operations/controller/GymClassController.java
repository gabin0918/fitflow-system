package com.fitflow.gym_operations.controller;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.dto.GymClassRequestDTO;
import com.fitflow.gym_operations.service.GymClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gym/classes")
@RequiredArgsConstructor
public class GymClassController {

    private final GymClassService service;
    private final RestTemplate restTemplate;

    // Pobieranie grafiku
    @GetMapping
    public ResponseEntity<List<GymClass>> getAllClasses() {
        return ResponseEntity.ok(service.getAllClasses());
    }

    // Dodawanie nowych zajęć z wybraniem trenera
    @PostMapping
    public ResponseEntity<?> addClass(@RequestBody GymClassRequestDTO request) {
        try {
            GymClass gymClass = service.addClass(request);
            return ResponseEntity.ok(gymClass);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Pobieranie listy dostępnych trenerów
    @GetMapping("/available-trainers")
    public ResponseEntity<?> getAvailableTrainers() {
        try {
            System.out.println("Getting available trainers from auth-service...");

            // Pobierz listę użytkowników z rolą ROLE_TRAINER z auth-service
            ResponseEntity<?> response = restTemplate.getForEntity(
                    "http://localhost:8081/api/auth/trainers",
                    Object.class
            );

            System.out.println("Auth-service response status: " + response.getStatusCode());
            System.out.println("Auth-service response body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                System.err.println("Auth-service error: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode())
                        .body("Nie udało się pobrać listy trenerów z auth-service");
            }
        } catch (Exception e) {
            System.err.println("Error communicating with auth-service: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Błąd komunikacji z serwisem autentykacji: " + e.getMessage());
        }
    }
}