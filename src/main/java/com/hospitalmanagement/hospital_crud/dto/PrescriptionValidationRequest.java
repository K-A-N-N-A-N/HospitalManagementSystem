package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionValidationRequest {
    private String prescriptionId;
    private String patientId;
    private List<PrescriptionValidationItem> items;
}
