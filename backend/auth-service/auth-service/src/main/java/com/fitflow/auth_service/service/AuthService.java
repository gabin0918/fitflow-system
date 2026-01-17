package com.fitflow.auth_service.service;

import com.fitflow.auth_service.dto.LoginRequestDTO;
import com.fitflow.auth_service.dto.UserRegistrationDTO;
import com.fitflow.auth_service.model.Role;
import com.fitflow.auth_service.model.User;
import com.fitflow.auth_service.repository.RoleRepository;
import com.fitflow.auth_service.repository.UserRepository;
import com.fitflow.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Metoda Rejestracji
    public void registerUser(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email jest już zajęty!");
        }

        // LOGIKA RÓL:
        Set<Role> roles = new HashSet<>();

        // Każdy użytkownik dostaje ROLE_CLIENT
        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Rola ROLE_CLIENT nie istnieje w bazie!"));
        roles.add(clientRole);

        // Jeśli zaznaczył checkbox isTrainer, dostaje ROLE_TRAINER
        if (dto.isTrainer()) {
            Role trainerRole = roleRepository.findByName("ROLE_TRAINER")
                    .orElseThrow(() -> new RuntimeException("Rola ROLE_TRAINER nie istnieje w bazie!"));
            roles.add(trainerRole);
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .roles(roles)
                .build();

        userRepository.save(user);
    }

    // Metoda Logowania
    public String login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy email lub hasło"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Nieprawidłowy email lub hasło");
        }


        return jwtService.generateToken(user.getEmail(), user.getId(), user.getRoles());
    }
}