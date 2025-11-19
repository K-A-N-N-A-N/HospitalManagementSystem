package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentSummaryDTOTest {

    AppointmentSummaryDTO dto;

    @BeforeEach
    void setUp() {
        dto = new AppointmentSummaryDTO();
        dto.setAppointmentId("A1");
        dto.setDoctorId("D1");
        dto.setDoctorName("Dr. Who");
        dto.setPatientId("P1");
        dto.setPatientName("Jane Doe");
        dto.setReasonForVisit("Follow up");
        dto.setAppointmentDate(LocalDateTime.of(2025, 10, 28, 9, 30));
        dto.setStatus("BOOKED");
        dto.setPrescriptionId("PR1");
        dto.setMedicines(List.of(new PrescriptionItemDTO("PI1", "Med", "10mg", "Once")));
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("A1", dto.getAppointmentId());
        assertEquals("D1", dto.getDoctorId());
        assertEquals("Dr. Who", dto.getDoctorName());
        assertEquals("P1", dto.getPatientId());
        assertEquals("Jane Doe", dto.getPatientName());
        assertEquals("Follow up", dto.getReasonForVisit());
        assertEquals(LocalDateTime.of(2025, 10, 28, 9, 30), dto.getAppointmentDate());
        assertEquals("BOOKED", dto.getStatus());
        assertEquals("PR1", dto.getPrescriptionId());
        assertNotNull(dto.getMedicines());
        assertEquals(1, dto.getMedicines().size());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        AppointmentSummaryDTO dto2 = new AppointmentSummaryDTO();
        dto2.setAppointmentId(dto.getAppointmentId());
        dto2.setDoctorId(dto.getDoctorId());
        dto2.setDoctorName(dto.getDoctorName());
        dto2.setPatientId(dto.getPatientId());
        dto2.setPatientName(dto.getPatientName());
        dto2.setReasonForVisit(dto.getReasonForVisit());
        dto2.setAppointmentDate(dto.getAppointmentDate());
        dto2.setStatus(dto.getStatus());
        dto2.setPrescriptionId(dto.getPrescriptionId());
        dto2.setMedicines(dto.getMedicines());

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }
}