package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryDTO {

    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String reasonForVisit;
    private LocalDateTime appointmentDate;
    private String status;
    private Long prescriptionId;
    private List<PrescriptionItemDTO> medicines;

}
