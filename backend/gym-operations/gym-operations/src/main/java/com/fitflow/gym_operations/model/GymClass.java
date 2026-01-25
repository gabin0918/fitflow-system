package com.fitflow.gym_operations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gym_classes")
public class GymClass {

    @Transient // Ta adnotacja sprawia, że pole NIE zostanie stworzone w bazie danych
    @JsonProperty("availableSpots") // Taką nazwę zobaczy React
    private long availableSpots;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // np. "Joga", "Crossfit"

    @Column(nullable = false)
    private Long trainerId; // ID trenera z auth-service

    @Transient // To pole jest tylko do przesyłania danych (nie zapisuje się w bazie)
    @JsonProperty("trainerName")
    private String trainerName; // Imię trenera - pobierane z auth-service

    @Column(nullable = false)
    private LocalDateTime dateTime; // Kiedy odbywają się zajęcia

    @Column(nullable = false)
    private int capacity; // Ile osób może się zapisać
}