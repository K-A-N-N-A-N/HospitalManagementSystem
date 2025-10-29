package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;

@Data
public class PrescriptionItemDTO {
    private Long id;
    private String medicineName;
    private String dosage;
    private String notes;
}
