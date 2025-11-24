package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    //@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping("active")
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllActiveDoctors();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("active/{id}")
    public Doctor getDoctorById(@PathVariable String id) {
        return doctorService.getActiveDoctorById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("inactive")
    public List<Doctor> getDoctorByIdInactive() {return doctorService.getAllInactiveDoctors();}

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public Doctor createDoctor(@Valid @RequestBody Doctor doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable String id,@Valid @RequestBody Doctor doctor) {
        return doctorService.updateDoctor(id, doctor);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("delete/{id}")
    public String softDeleteDoctor(@PathVariable String id) {
        doctorService.softDeleteDoctor(id);
        return "Doctor status set to Inactive.";
    }

    /*
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("delete{id}")
    public String deleteDoctorById(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return "Doctor deleted successfully.";
    }
     */
}
