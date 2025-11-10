package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping
    public List<PrescriptionDTO> getALlPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{id}")
    public PrescriptionDTO getPrescriptionById(@PathVariable String id) {
        return prescriptionService.getPrescriptionById(id);
    }

    @PostMapping
    public PrescriptionDTO addPrescription(@RequestBody PrescriptionDTO prescription) {
        return prescriptionService.createPrescription(prescription);
    }

    @PutMapping("/{id}")
    public PrescriptionDTO updatePrescription(@PathVariable String id, @RequestBody PrescriptionDTO prescription) {
        return prescriptionService.updatePrescription(id, prescription);
    }

    @DeleteMapping("/{id}")
    public String deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return "Prescription deleted successfully";
    }

}
