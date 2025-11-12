package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final JmsTemplate jmsTemplate;

    public DoctorService(DoctorRepository doctorRepository, JmsTemplate jmsTemplate) {
        this.doctorRepository = doctorRepository;
        this.jmsTemplate = jmsTemplate;
    }

    // Get all Active doctors
    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    // Get All Inactive doctors
    public List<Doctor> getAllInactiveDoctors() { return doctorRepository.findByActiveFalse(); }

    // Get Active Doctor by id
    public Doctor getActiveDoctorById(String id) {
        return doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a different Doctor id"));
    }

    // Create doctor via ActiveMQ queue
    public Doctor createDoctor(Doctor doctor) {
        try {
            jmsTemplate.convertAndSend("doctor.queue", doctor);
            System.out.println("📤 Sent Doctor to queue: " + doctor.getName());
            return doctor;
        } catch (Exception e) {
            throw new SystemOperationException("Failed to send Doctor data to ActiveMQ", e);
        }
    }

    public Doctor updateDoctor(String id, Doctor updatedDoctor) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a different Doctor id"));

        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setPhotoPath(updatedDoctor.getPhotoPath());

        return doctorRepository.save(existingDoctor);
    }

    public void softDeleteDoctor(String id) {
        Doctor existingDoctor = doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found or already Inactive"));
        existingDoctor.setActive(false);
        doctorRepository.save(existingDoctor);
    }

    /*
    public void deleteDoctor(String id) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a different Doctor id"));
        doctorRepository.deleteById(id);
    }
     */
}
