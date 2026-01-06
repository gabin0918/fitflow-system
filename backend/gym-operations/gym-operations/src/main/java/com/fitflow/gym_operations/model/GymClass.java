package com.fitflow.gym_operations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gym_classes")
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // np. "Joga", "Crossfit"

    @Column(nullable = false)
    private String trainerName; // Imię trenera

    @Column(nullable = false)
    private LocalDateTime dateTime; // Kiedy odbywają się zajęcia

    @Column(nullable = false)
    private int capacity; // Ile osób może się zapisać
}