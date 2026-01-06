package com.fitflow.gym_operations.controller;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.service.GymClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gym/classes")
@RequiredArgsConstructor
public class GymClassController {

    private final GymClassService service;

    // Pobieranie grafiku
    @GetMapping
    public ResponseEntity<List<GymClass>> getAllClasses() {
        return ResponseEntity.ok(service.getAllClasses());
    }

    // Dodawanie nowych zajęć
    @PostMapping
    public ResponseEntity<GymClass> addClass(@RequestBody GymClass gymClass) {
        return ResponseEntity.ok(service.addClass(gymClass));
    }
}