package com.fitflow.auth_service.service;

import com.fitflow.auth_service.dto.LoginRequestDTO;
import com.fitflow.auth_service.dto.UserRegistrationDTO;
import com.fitflow.auth_service.dto.TrainerDTO;
import com.fitflow.auth_service.model.Role;
import com.fitflow.auth_service.model.User;
import com.fitflow.auth_service.repository.RoleRepository;
import com.fitflow.auth_service.repository.UserRepository;
import com.fitflow.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Pobierz użytkownika po ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik o ID " + id + " nie istnieje"));
    }

    // Pobierz listę trenerów (użytkowników z rolą ROLE_TRAINER)
    public List<TrainerDTO> getTrainersList() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "ROLE_TRAINER".equals(role.getName())))
                .map(user -> TrainerDTO.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .bio(user.getBio())
                        .phoneNumber(user.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());
    }
}