package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.UserRole;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.userRepository;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdminUser(
            userRepository userRepository,
            BCryptPasswordEncoder encoder,
            @Value("${app.admin.name}") String adminName,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {
        return args -> {
            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            userEntity admin = new userEntity();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        };
    }
}
