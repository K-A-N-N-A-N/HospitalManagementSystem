package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionValidationResponse {
    private String prescriptionId;
    private List<Map<String, Object>> validation;
}
