package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentRequestTest {

    AppointmentRequest dto;

    @BeforeEach
    void setUp() {
        dto = new AppointmentRequest();
        dto.setDoctorId("D1");
        dto.setPatientId("P1");
        dto.setAppointmentTime(LocalDateTime.of(2025, 10, 28, 9, 30));
        dto.setReason("Consultation");
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("D1", dto.getDoctorId());
        assertEquals("P1", dto.getPatientId());
        assertEquals(LocalDateTime.of(2025, 10, 28, 9, 30), dto.getAppointmentTime());
        assertEquals("Consultation", dto.getReason());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        AppointmentRequest dto2 = new AppointmentRequest("D1", "P1", LocalDateTime.of(2025, 10, 28, 9, 30), "Consultation");
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }
}