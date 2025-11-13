package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DoctorListener {

    private final DoctorRepository doctorRepository;

    public DoctorListener(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    @JmsListener(destination = "doctor.queue")
    public void handleCreate(Doctor doctor) {
        log.info("[CREATE] Received doctor: {}", doctor.getName());
        doctorRepository.save(doctor);
        log.info("Doctor created: {}", doctor.getName());
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
