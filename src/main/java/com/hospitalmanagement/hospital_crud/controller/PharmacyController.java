package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.service.PharmacyIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyIntegrationService pharmacyService;

    @GetMapping("/medicines/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(pharmacyService.getAllMedicines());
    }

    @GetMapping("/medicines/sku/{sku}")
    public ResponseEntity<?> getLite(@PathVariable String sku) {
        return ResponseEntity.ok(pharmacyService.getLiteMedicine(sku));
    }
}
