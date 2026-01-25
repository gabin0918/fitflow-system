package com.fitflow.membership_payment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // "BASIC", "PREMIUM", "VIP"

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays; // czas trwania w dniach (np. 30, 90, 365)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 1000)
    private String features; // lista funkcji oddzielona przecinkami
}
