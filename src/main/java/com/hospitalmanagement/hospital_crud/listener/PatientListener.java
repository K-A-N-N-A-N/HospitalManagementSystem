package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PatientListener {

    private final PatientRepository patientRepository;

    public PatientListener(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    @JmsListener(destination = "patient.queue")
    public void handleCreate(Patient patient){
        System.out.println("[CREATE] Received Patient: " + patient.getName());
        patientRepository.save(patient);
        System.out.println("Patient Created : " + patient.getName());
    }

    @Transactional
    @JmsListener(destination = "patient.update.queue")
    public void handleUpdate(Patient patient){
        System.out.println("[UPDATE] Received Patient: " + patient.getName());
        patientRepository.save(patient);
        System.out.println("Patient Updated : " + patient.getName());
    }

    @Transactional
    @JmsListener(destination = "patient.delete.queue")
    public void handleDelete(Patient patient){
        System.out.println("[DELETE] Received Patient: " + patient.getName());
        patient.setActive(false);
        patientRepository.save(patient);
        System.out.println("Patient Soft Deleted : " + patient.getName());
    }
}
