package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;
import com.hospitalmanagement.hospital_crud.service.QueueService;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final JmsTemplate jmsTemplate;
    private final QueueService queueService;

    public DoctorService(DoctorRepository doctorRepository, JmsTemplate jmsTemplate, QueueService queueService) {
        this.doctorRepository = doctorRepository;
        this.jmsTemplate = jmsTemplate;
        this.queueService = queueService;
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
        queueService.sendToQueue("doctor.queue", doctor);
        return doctor;
    }

    public Doctor updateDoctor(String id, Doctor updatedDoctor) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a different Doctor id"));

        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setPhotoPath(updatedDoctor.getPhotoPath());

        queueService.sendToQueue("doctor.update.queue", existingDoctor);
        return existingDoctor;
    }

    public void softDeleteDoctor(String id) {
        Doctor existingDoctor = doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found or already Inactive"));
        existingDoctor.setActive(false);
        queueService.sendToQueue("doctor.delete.queue", existingDoctor);
    }

    /*
    public void deleteDoctor(String id) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a different Doctor id"));
        doctorRepository.deleteById(id);
    }
     */
}
