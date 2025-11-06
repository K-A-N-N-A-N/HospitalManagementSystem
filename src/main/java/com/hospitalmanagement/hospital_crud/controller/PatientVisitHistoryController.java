package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.PatientVisitHistoryDTO;
import com.hospitalmanagement.hospital_crud.service.PatientService;
import com.hospitalmanagement.hospital_crud.service.PatientVisitHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patientVisitHistory")
public class PatientVisitHistoryController {

    private final PatientVisitHistoryService patientVisitHistoryService;

    public PatientVisitHistoryController(PatientVisitHistoryService patientVisitHistoryService) {
        this.patientVisitHistoryService = patientVisitHistoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PatientVisitHistoryDTO>> getPatientVisitHistory(@PathVariable Long id) {
        List<PatientVisitHistoryDTO> histories = patientVisitHistoryService.getVisitHistoryByPatient(id);
        return ResponseEntity.ok(histories);
    }

}
