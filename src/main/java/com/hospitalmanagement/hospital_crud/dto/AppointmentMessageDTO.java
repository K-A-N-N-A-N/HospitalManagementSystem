package com.hospitalmanagement.hospital_crud.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppointmentMessageDTO implements Serializable {
    private String doctorId;
    private String patientId;
    private String slotId;
    private LocalDateTime appointmentTime;
    private String reason;
}
