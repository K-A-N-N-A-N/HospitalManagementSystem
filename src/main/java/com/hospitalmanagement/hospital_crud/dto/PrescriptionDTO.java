package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class PrescriptionDTO {
    private Long id;
    private Long appointment_id;
    private List<PrescriptionItemDTO> medicines;
    private Instant createdAt;
    private Instant updatedAt;
}
