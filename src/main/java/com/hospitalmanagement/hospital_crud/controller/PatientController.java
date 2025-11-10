package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("active")
    public List<Patient> getAllPatients() {
        return patientService.getAllActivePatients();
    }

    @GetMapping("active/{id}")
    public Patient getPatientById(@PathVariable UUID id) {
        return patientService.getActivePatientById(id);
    }

    @GetMapping("inactive")
    public List<Patient> getPatientByIdInactive() {
        return patientService.getAllInactivePatients();
    }

    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable UUID id,@Valid @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient);
    }

    @DeleteMapping("delete/{id}")
    public String softDeletePatient(@PathVariable UUID id) {
        patientService.softDeletePatient(id);
        return "Patient status set to Inactive.";
    }
/*
    @DeleteMapping("delete/{id}")
    public String deletePatientById(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "Patient deleted successfully.";
    }
*/
}
