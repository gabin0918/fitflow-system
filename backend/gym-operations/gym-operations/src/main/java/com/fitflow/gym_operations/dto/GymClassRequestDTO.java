package com.fitflow.gym_operations.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymClassRequestDTO {
    private String name; // Nazwa zajęć (np. "Joga", "Crossfit")
    private Long trainerId; // ID trenera
    private LocalDateTime dateTime; // Kiedy odbywają się zajęcia
    private int capacity; // Pojemność zajęć
}
