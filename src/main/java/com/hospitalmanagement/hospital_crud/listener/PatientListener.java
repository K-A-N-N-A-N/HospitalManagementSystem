package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.entity.Role;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PatientListener {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PatientListener(
            PatientRepository patientRepository,
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @JmsListener(destination = "patient.queue")
    public void handleCreate(Patient patient){
        log.info("[CREATE] Received Patient: {}", patient.getName());
        patientRepository.save(patient);
        log.info("Patient Created : {}", patient.getName());

        if (patient.getUsername() != null && patient.getRawPassword() != null) {

            User user = new User();
            user.setUsername(patient.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(patient.getRawPassword()));
            user.setRole(Role.PATIENT);
            user.setDoctorId(patient.getId());

            userRepository.save(user);

            log.info("User created for doctor: {}", patient.getUsername());
        }
    }

    @Transactional
    @JmsListener(destination = "patient.update.queue")
    public void handleUpdate(Patient patient){
        log.info("[UPDATE] Received Patient: {}", patient.getName());
        patientRepository.save(patient);
        log.info("Patient Updated : {}", patient.getName());
    }

    @Transactional
    @JmsListener(destination = "patient.delete.queue")
    public void handleDelete(Patient patient){
        log.info("[DELETE] Received Patient: {}",  patient.getName());
        patient.setActive(false);
        patientRepository.save(patient);
        log.info("Patient Soft Deleted : {}", patient.getName());
    }
}
