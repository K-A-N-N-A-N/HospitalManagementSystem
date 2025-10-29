package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SlotRequestDTO {
    private Long doctorId;
    private LocalDate date;
}
