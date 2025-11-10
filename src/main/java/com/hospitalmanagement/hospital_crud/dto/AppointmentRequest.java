package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private String doctorId;
    private String patientId;
    private LocalDateTime appointmentTime;
    private String reason;
}
