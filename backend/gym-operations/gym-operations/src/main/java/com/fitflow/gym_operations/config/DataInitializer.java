package com.fitflow.gym_operations.config;

import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GymClassRepository gymClassRepository;

    @Override
    public void run(String... args) {
        if (gymClassRepository.count() == 0) {

            // Marek (ID 1)
            gymClassRepository.save(GymClass.builder()
                    .name("Crossfit Power").trainerId(1L)
                    .dateTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0))
                    .capacity(10).build());

            // Anna (ID 2)
            gymClassRepository.save(GymClass.builder()
                    .name("Yoga Zen").trainerId(2L)
                    .dateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                    .capacity(20).build());

            // Robert (ID 3)
            gymClassRepository.save(GymClass.builder()
                    .name("Boks - Początkujący").trainerId(3L)
                    .dateTime(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                    .capacity(12).build());

            // Kasia (ID 4)
            gymClassRepository.save(GymClass.builder()
                    .name("Zumba Energy").trainerId(4L)
                    .dateTime(LocalDateTime.now().plusDays(2).withHour(17).withMinute(30))
                    .capacity(25).build());

            System.out.println(">> [SEED OPS] Pełny grafik na 4 trenerów gotowy!");
        }
    }
}