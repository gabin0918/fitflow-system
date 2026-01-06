package com.fitflow.gym_operations.service;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymClassService {

    private final GymClassRepository repository;

    // Metoda 1: Pobierz wszystkie zajęcia (do grafiku)
    public List<GymClass> getAllClasses() {
        return repository.findAll();
    }

    // Metoda 2: Dodaj nowe zajęcia (dla trenera/admina)
    public GymClass addClass(GymClass gymClass) {
        return repository.save(gymClass);
    }
}