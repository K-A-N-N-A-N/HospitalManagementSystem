package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping("active")
    public List<Patient> getAllPatients() {
        return patientService.getAllActivePatients();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping("active/{id}")
    public Patient getPatientById(@PathVariable String id) {
        return patientService.getActivePatientById(id);
    }

    @GetMapping("inactive")
    public List<Patient> getPatientByIdInactive() {
        return patientService.getAllInactivePatients();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable String id, @Valid @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("delete/{id}")
    public String softDeletePatient(@PathVariable String id) {
        patientService.softDeletePatient(id);
        return "Patient status set to Inactive.";
    }
/*
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("delete/{id}")
    public String deletePatientById(@PathVariable String id) {
        patientService.deletePatient(id);
        return "Patient deleted successfully.";
    }
*/
}
