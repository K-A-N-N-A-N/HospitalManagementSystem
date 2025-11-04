package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotRequestDTO {
    private Long doctorId;
    private LocalDate date;
    private int durationMinutes;
}
