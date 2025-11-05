package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("active")
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllActiveDoctors();
    }

    @GetMapping("active/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.getActiveDoctorById(id);
    }

    @GetMapping("inactive")
    public List<Doctor> getDoctorByIdInactive() {return doctorService.getAllInactiveDoctors();}

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        return doctorService.updateDoctor(id, doctor);
    }

    @DeleteMapping("delete/{id}")
    public String softDeleteDoctor(@PathVariable Long id) {
        doctorService.softDeleteDoctor(id);
        return "Doctor status set to Inactive.";
    }

    /*
    @DeleteMapping("delete{id}")
    public String deleteDoctorById(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "Doctor deleted successfully.";
    }
     */
}
