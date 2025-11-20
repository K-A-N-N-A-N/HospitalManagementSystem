package com.hospitalmanagement.hospital_crud.config;

import com.hospitalmanagement.hospital_crud.entity.Role;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdmin() {

        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("Admin has been created");
        }
    }
}
