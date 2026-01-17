package com.fitflow.auth_service.config;

import com.fitflow.auth_service.model.Role;
import com.fitflow.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initializeRoles() {
        return args -> {
            // Sprawdzamy czy role już istnieją
            if (roleRepository.findByName("ROLE_CLIENT").isEmpty()) {
                Role clientRole = Role.builder()
                        .name("ROLE_CLIENT")
                        .description("Klient siłowni - może się zapisywać na zajęcia")
                        .build();
                roleRepository.save(clientRole);
                System.out.println("✓ Rola ROLE_CLIENT utworzona");
            }

            if (roleRepository.findByName("ROLE_TRAINER").isEmpty()) {
                Role trainerRole = Role.builder()
                        .name("ROLE_TRAINER")
                        .description("Trener - może dodawać zajęcia")
                        .build();
                roleRepository.save(trainerRole);
                System.out.println("✓ Rola ROLE_TRAINER utworzona");
            }

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = Role.builder()
                        .name("ROLE_ADMIN")
                        .description("Administrator - pełny dostęp")
                        .build();
                roleRepository.save(adminRole);
                System.out.println("✓ Rola ROLE_ADMIN utworzona");
            }
        };
    }
}
