package com.hospitalmanagement.hospital_crud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateSlotDTO {
    private Long doctorId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2025-10-28")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "09:00:00")
    private LocalTime startTime;

    private boolean available;
}