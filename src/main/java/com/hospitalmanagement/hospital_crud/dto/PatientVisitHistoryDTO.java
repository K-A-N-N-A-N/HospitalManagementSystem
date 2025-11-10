package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientVisitHistoryDTO {

    private String id;
    private String patientId;
    private String patientName;
    private LocalDateTime visitDate;
    private AppointmentSummaryDTO appointmentSummary;
}
