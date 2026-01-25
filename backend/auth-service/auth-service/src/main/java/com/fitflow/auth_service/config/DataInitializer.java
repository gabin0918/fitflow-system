package com.fitflow.auth_service.config;

import com.fitflow.auth_service.model.Role;
import com.fitflow.auth_service.model.User;
import com.fitflow.auth_service.repository.RoleRepository;
import com.fitflow.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ROLE_CLIENT").description("Klient").build());
            roleRepository.save(Role.builder().name("ROLE_TRAINER").description("Trener").build());
            roleRepository.save(Role.builder().name("ROLE_ADMIN").description("Admin").build());
            System.out.println(">> [SEED] Role dodane.");
        }

        if (userRepository.count() == 0) {
            Role trainerRole = roleRepository.findByName("ROLE_TRAINER").orElse(null);

            userRepository.save(User.builder()
                    .firstName("Marek").lastName("Sila").email("marek@fit.pl")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(trainerRole)).build());

            userRepository.save(User.builder()
                    .firstName("Anna").lastName("Fit").email("anna@fit.pl")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(trainerRole)).build());

            userRepository.save(User.builder()
                    .firstName("Robert").lastName("Cios").email("robert@fit.pl")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(trainerRole)).build());

            userRepository.save(User.builder()
                    .firstName("Kasia").lastName("Taniec").email("kasia@fit.pl")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(trainerRole)).build());

            System.out.println(">> [SEED] Trenerzy Marek, Anna, Robert i Kasia dodani.");
        }
    }
}