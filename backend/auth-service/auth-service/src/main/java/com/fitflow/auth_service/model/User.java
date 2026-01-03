package com.fitflow.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data // To generuje gettery, settery, toString (dzięki Lombok)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // W bazie tabela będzie się nazywać "users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Zahaszowane hasło

    private String firstName;
    private String lastName;

    // Prosta obsługa ról jako String (np. "ROLE_CLIENT", "ROLE_ADMIN")
    // W raporcie masz relację do tabeli Role, ale na start zrobimy to prościej,
    // żeby szybko zadziałało. Potem wydzielimy Entity Roli.
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;
}