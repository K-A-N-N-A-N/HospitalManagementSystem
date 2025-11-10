package com.hospitalmanagement.hospital_crud.dto;

import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSlotResponse {
    private String id;
    private String doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public DoctorSlotResponse(DoctorSlot slot) {
        this.id = slot.getId();
        this.doctorId = slot.getDoctor().getId();
        this.date = slot.getDate();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.available = slot.isAvailable();
    }
}
