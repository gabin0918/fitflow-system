package com.fitflow.gym_operations.service;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.repository.BookingRepository; // Dodano
import com.fitflow.gym_operations.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymClassService {

    private final GymClassRepository repository;
    private final BookingRepository bookingRepository; // Wstrzykujemy repozytorium rezerwacji

    public List<GymClass> getAllClasses() {
        List<GymClass> classes = repository.findAll();

        // Dla każdych zajęć obliczamy wolne miejsca
        return classes.stream().map(c -> {
            long taken = bookingRepository.countByGymClassId(c.getId());
            c.setAvailableSpots(c.getCapacity() - taken);
            return c;
        }).collect(Collectors.toList());
    }

    public GymClass addClass(GymClass gymClass) {
        return repository.save(gymClass);
    }
}