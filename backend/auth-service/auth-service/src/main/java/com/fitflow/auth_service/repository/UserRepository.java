package com.fitflow.auth_service.repository;

import com.fitflow.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Metoda do szukania użytkownika po emailu (do logowania)
    Optional<User> findByEmail(String email);

    // Metoda do sprawdzania, czy email już istnieje (przy rejestracji)
    boolean existsByEmail(String email);
}