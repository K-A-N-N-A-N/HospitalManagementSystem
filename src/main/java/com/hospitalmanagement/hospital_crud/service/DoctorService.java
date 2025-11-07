package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // Get all Active doctors
    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    // Get All Inactive doctors
    public List<Doctor> getAllInactiveDoctors() { return doctorRepository.findByActiveFalse(); }

    // Get Active Doctor by Id
    public Doctor getActiveDoctorById(Long id) {
        return doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a differnt Doctor id"));
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a differnt Doctor id"));

        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        //existingDoctor.setContactInfo(updatedDoctor.getContactInfo());
        existingDoctor.setPhotoPath(updatedDoctor.getPhotoPath());

        return doctorRepository.save(existingDoctor);
    }

    public void softDeleteDoctor(Long id) {
        Doctor existingDoctor = doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found or already Inactive"));
        existingDoctor.setActive(false);
        doctorRepository.save(existingDoctor);
    }

    public void deleteDoctor(Long id) {
        Doctor excistingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found, Try a differnt Doctor id"));
        doctorRepository.deleteById(id);
    }
}
