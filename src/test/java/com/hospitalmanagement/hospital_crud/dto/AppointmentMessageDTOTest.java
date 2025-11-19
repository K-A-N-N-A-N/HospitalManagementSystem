package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentMessageDTOTest {

    AppointmentMessageDTO dto;

    @BeforeEach
    void setUp() {
        dto = new AppointmentMessageDTO();
        dto.setDoctorId("D1");
        dto.setPatientId("P1");
        dto.setSlotId("S1");
        dto.setAppointmentTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        dto.setReason("Regular Checkup");
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFieldsCorrectly() {
        assertEquals("D1", dto.getDoctorId());
        assertEquals("P1", dto.getPatientId());
        assertEquals("S1", dto.getSlotId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 30), dto.getAppointmentTime());
        assertEquals("Regular Checkup", dto.getReason());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        AppointmentMessageDTO dto2 = new AppointmentMessageDTO();
        dto2.setDoctorId("D1");
        dto2.setPatientId("P1");
        dto2.setSlotId("S1");
        dto2.setAppointmentTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        dto2.setReason("Regular Checkup");

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("DTOs with different data should not be equal")
    void dto_equalsShouldReturnFalseForDifferentData() {
        AppointmentMessageDTO dto2 = new AppointmentMessageDTO();
        dto2.setDoctorId("DIFFERENT");

        assertNotEquals(dto, dto2);
    }

    @Test
    @DisplayName("toString method should not be null or empty")
    void dto_toStringShouldReturnValidString() {
        String result = dto.toString();
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}
