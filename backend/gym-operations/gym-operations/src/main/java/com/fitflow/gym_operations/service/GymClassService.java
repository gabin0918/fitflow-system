package com.fitflow.gym_operations.service;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.dto.GymClassRequestDTO;
import com.fitflow.gym_operations.repository.BookingRepository;
import com.fitflow.gym_operations.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymClassService {

    private final GymClassRepository repository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    public List<GymClass> getAllClasses() {
        List<GymClass> classes = repository.findAll();

        // Dla każdych zajęć obliczamy wolne miejsca i pobieramy dane trenera
        return classes.stream().map(c -> {
            long taken = bookingRepository.countByGymClassId(c.getId());
            c.setAvailableSpots(c.getCapacity() - taken);

            // Pobierz dane trenera z auth-service
            try {
                fetchTrainerName(c);
            } catch (Exception e) {
                c.setTrainerName("Nieznany trener");
            }

            return c;
        }).collect(Collectors.toList());
    }

    public GymClass addClass(GymClassRequestDTO request) {
        // Sprawdź, czy trener istnieje w auth-service
        if (!trainerExists(request.getTrainerId())) {
            throw new RuntimeException("Trener o ID " + request.getTrainerId() + " nie istnieje w systemie");
        }

        GymClass gymClass = GymClass.builder()
                .name(request.getName())
                .trainerId(request.getTrainerId())
                .dateTime(request.getDateTime())
                .capacity(request.getCapacity())
                .build();

        return repository.save(gymClass);
    }

    private boolean trainerExists(Long trainerId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "http://localhost:8081/api/auth/users/" + trainerId,
                    Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private void fetchTrainerName(GymClass gymClass) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "http://localhost:8081/api/auth/users/" + gymClass.getTrainerId(),
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> userBody = response.getBody();
                String firstName = (String) userBody.get("firstName");
                String lastName = (String) userBody.get("lastName");
                gymClass.setTrainerName(firstName + " " + lastName);
            }
        } catch (Exception e) {
            // Jeśli nie uda się pobrać, zostaw trenerName nullem
            gymClass.setTrainerName("Nieznany trener");
        }
    }
}