package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionValidationItem {
    private String sku;
    private int quantity;
}
