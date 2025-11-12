package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DoctorConsumerListener {

    private final DoctorRepository doctorRepository;

    public DoctorConsumerListener(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    @JmsListener(destination = "doctor.queue")
    public void consumeDoctor(Doctor doctor) {
        System.out.println("📩 Received doctor from queue: " + doctor.getName());
        doctorRepository.save(doctor);
        System.out.println("Saved doctor to DB: " + doctor.getName());
    }
}
