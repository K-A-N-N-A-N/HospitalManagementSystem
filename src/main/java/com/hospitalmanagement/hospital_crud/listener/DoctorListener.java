package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.Role;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DoctorListener {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DoctorListener(
            DoctorRepository doctorRepository,
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @JmsListener(destination = "doctor.queue")
    public void handleCreate(Doctor doctor) {
        log.info("[CREATE] Received doctor: {}", doctor.getName());
        doctorRepository.save(doctor);
        log.info("Doctor created: {}", doctor.getName());

        // Create user credentials if provided
        if (doctor.getUsername() != null && doctor.getRawPassword() != null) {

            User user = new User();
            user.setUsername(doctor.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(doctor.getRawPassword()));
            user.setRole(Role.DOCTOR);
            user.setDoctorId(doctor.getId());

            userRepository.save(user);

            log.info("User created for doctor: {}", doctor.getUsername());
        }
    }

    @Transactional
    @JmsListener(destination = "doctor.update.queue")
    public void handleUpdate(Doctor doctor) {
        log.info("[UPDATE] Received doctor: {}", doctor.getName());
        doctorRepository.save(doctor);
        log.info("Doctor updated: {}", doctor.getName());
    }

    @Transactional
    @JmsListener(destination = "doctor.delete.queue")
    public void handleDelete(Doctor doctor) {
        log.info("[DELETE] Received doctor: {}", doctor.getName());
        doctorRepository.save(doctor);
        log.info("Doctor deactivated: {}", doctor.getName());
    }
}
