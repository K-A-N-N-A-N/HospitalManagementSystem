package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SlotRequestDTO {
    private String doctorId;
    private LocalDate date;
    private int durationMinutes;
}
