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

    public void registerUser(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email jest już zajęty!");
        }

        Set<Role> roles = new HashSet<>();
        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Rola ROLE_CLIENT nie istnieje!"));
        roles.add(clientRole);

        // NASZA ZMIANA: Zabezpieczenie kodu trenera
        if (dto.isTrainer()) {
            if (!"FITFLOW2026".equals(dto.getAdminCode())) {
                throw new RuntimeException("Nieprawidłowy kod autoryzacji trenera!");
            }
            Role trainerRole = roleRepository.findByName("ROLE_TRAINER")
                    .orElseThrow(() -> new RuntimeException("Rola ROLE_TRAINER nie istnieje!"));
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

    public String login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy email lub hasło"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Nieprawidłowy email lub hasło");
        }
        return jwtService.generateToken(user.getEmail(), user.getId(), user.getRoles());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie ma usera"));
    }

    public List<TrainerDTO> getTrainersList() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_TRAINER".equals(r.getName())))
                .map(u -> TrainerDTO.builder().id(u.getId()).firstName(u.getFirstName()).lastName(u.getLastName()).build())
                .collect(Collectors.toList());
    }
}