package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {
    private String purchaseId;
    private String prescriptionId;
    private String patientId;
    private List<Map<String, Object>> payableItems;
    private Double totalAmount;
    private String status;
}