package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PatientListener {

    private final PatientRepository patientRepository;

    public PatientListener(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    @JmsListener(destination = "patient.queue")
    public void handleCreate(Patient patient){
        log.info("[CREATE] Received Patient: {}", patient.getName());
        patientRepository.save(patient);
        log.info("Patient Created : {}", patient.getName());
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
