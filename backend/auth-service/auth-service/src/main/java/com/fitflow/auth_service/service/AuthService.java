package com.fitflow.auth_service.service;

import com.fitflow.auth_service.dto.LoginRequestDTO;
import com.fitflow.auth_service.dto.UserRegistrationDTO;
import com.fitflow.auth_service.model.User;
import com.fitflow.auth_service.repository.UserRepository;
import com.fitflow.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // <--- Nowe pole (generator tokenów)

    // Metoda Rejestracji (ta już była)
    public void registerUser(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email jest już zajęty!");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .roles(Set.of("ROLE_CLIENT"))
                .build();

        userRepository.save(user);
    }

    // Metoda Logowania (NOWA)
    public String login(LoginRequestDTO dto) {
        // 1. Szukamy użytkownika w bazie
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy email lub hasło"));

        // 2. Sprawdzamy czy hasło pasuje (to zaszyfrowane z tym podanym)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Nieprawidłowy email lub hasło");
        }

        // 3. Jeśli wszystko ok -> generujemy token
        return jwtService.generateToken(user.getEmail());
    }
}