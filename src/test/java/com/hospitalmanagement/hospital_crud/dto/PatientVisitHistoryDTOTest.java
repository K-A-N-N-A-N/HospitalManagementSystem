package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PatientVisitHistoryDTOTest {

    PatientVisitHistoryDTO dto;

    @BeforeEach
    void setUp() {
        AppointmentSummaryDTO summary = new AppointmentSummaryDTO();
        summary.setAppointmentId("A1");
        summary.setDoctorId("D1");
        summary.setDoctorName("Dr. Smith");
        summary.setPatientId("P1");
        summary.setPatientName("Jane Doe");
        summary.setReasonForVisit("Checkup");
        summary.setAppointmentDate(LocalDateTime.of(2025, 10, 28, 9, 30));
        summary.setStatus("COMPLETED");

        dto = PatientVisitHistoryDTO.builder()
                .id("PVH1")
                .patientId("P1")
                .patientName("Jane Doe")
                .visitDate(LocalDateTime.of(2025, 10, 28, 9, 30))
                .appointmentSummary(summary)
                .build();
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("PVH1", dto.getId());
        assertEquals("P1", dto.getPatientId());
        assertEquals("Jane Doe", dto.getPatientName());
        assertEquals(LocalDateTime.of(2025, 10, 28, 9, 30), dto.getVisitDate());
        assertNotNull(dto.getAppointmentSummary());
        assertEquals("A1", dto.getAppointmentSummary().getAppointmentId());
    }

    @Test
    @DisplayName("toString method should not be null or empty")
    void dto_toStringShouldReturnValidString() {
        String result = dto.toString();
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}