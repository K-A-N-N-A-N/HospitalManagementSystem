package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Get all Active patients
    public List<Patient> getAllActivePatients() {
        return patientRepository.findByActiveTrue();
    }

    // Get All Inactive patients
    public List<Patient> getAllInactivePatients() {
        return patientRepository.findByActiveFalse();
    }

    // Get Active Patient by id
    public Patient getActivePatientById(String id) {
        return patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    public  Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(String id, Patient updatedPatient) {
        Patient excistingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        excistingPatient.setName(updatedPatient.getName());
        excistingPatient.setAddress(updatedPatient.getAddress());
        excistingPatient.setAge(updatedPatient.getAge());
        excistingPatient.setGender(updatedPatient.getGender());
        excistingPatient.setEmail(updatedPatient.getEmail());
        excistingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());

        return patientRepository.save(excistingPatient);
    }

    public void softDeletePatient(String id) {
        Patient existingPatient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found or already Inactive"));
        existingPatient.setActive(false);
        patientRepository.save(existingPatient);
    }

    public void deletePatient(String id) {
        Patient excistingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        patientRepository.delete(excistingPatient);
    }
}
