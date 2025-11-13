package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DoctorListener {

    private final DoctorRepository doctorRepository;

    public DoctorListener(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    @JmsListener(destination = "doctor.queue")
    public void handleCreate(Doctor doctor) {
        System.out.println("[CREATE] Received doctor: " + doctor.getName());
        doctorRepository.save(doctor);
        System.out.println("Doctor created: " + doctor.getName());
    }

    @Transactional
    @JmsListener(destination = "doctor.update.queue")
    public void handleUpdate(Doctor doctor) {
        System.out.println("[UPDATE] Received doctor: " + doctor.getName());
        doctorRepository.save(doctor);
        System.out.println("Doctor updated: " + doctor.getName());
    }

    @Transactional
    @JmsListener(destination = "doctor.delete.queue")
    public void handleDelete(Doctor doctor) {
        System.out.println("[DELETE] Received doctor: " + doctor.getName());
        doctorRepository.save(doctor);
        System.out.println("Doctor deactivated: " + doctor.getName());
    }
}
