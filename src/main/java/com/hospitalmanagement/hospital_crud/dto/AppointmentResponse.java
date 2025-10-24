package com.hospitalmanagement.hospital_crud.dto;

import com.hospitalmanagement.hospital_crud.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private Instant appointmentTime;
    private String reason;
    private String status;

    // constructor
    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getId();
        this.doctorId = appointment.getDoctor().getId();
        this.patientId = appointment.getPatient().getId();
        this.appointmentTime = appointment.getAppointmentTime();
        this.reason = appointment.getReason();
        this.status = appointment.getStatus().name();
    }
}

