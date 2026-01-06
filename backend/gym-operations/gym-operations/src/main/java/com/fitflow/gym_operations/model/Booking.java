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
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // Przechowujemy tylko ID użytkownika (bo User jest w innej bazie!)

    @ManyToOne
    @JoinColumn(name = "gym_class_id", nullable = false)
    private GymClass gymClass; // Relacja do zajęć

    private LocalDateTime bookingDate; // Kiedy dokonano rezerwacji

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now(); // Automatycznie ustaw datę przy zapisie
    }
}