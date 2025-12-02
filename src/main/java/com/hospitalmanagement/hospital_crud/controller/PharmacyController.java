package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.service.PharmacyIntegrationService;
import com.hospitalmanagement.hospital_crud.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyIntegrationService pharmacyService;
    private final PrescriptionService prescriptionService;

    @GetMapping("/medicines/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(pharmacyService.getAllMedicines());
    }

    @GetMapping("/medicines/sku/{sku}")
    public ResponseEntity<?> getLite(@PathVariable String sku) {
        return ResponseEntity.ok(pharmacyService.getLiteMedicine(sku));
    }

    @PostMapping("/validate-prescription/{prescriptionId}")
    public ResponseEntity<?> validatePrescription(@PathVariable String prescriptionId) {

        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(prescriptionId);

        List<Map<String, Object>> items = prescription.getMedicines().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sku", item.getSku());
                    map.put("quantity", item.getQuantity());
                    return map;
                })
                .toList();

        Map<String, Object> payload = Map.of(
                "prescriptionId", prescriptionId,
                "items", items
        );

        Map<String, Object> result = pharmacyService.validatePrescription(payload);

        return ResponseEntity.ok(result);
    }


}
