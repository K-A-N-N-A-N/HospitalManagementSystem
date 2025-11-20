package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class UpdateSlotDTOTest {

    private UpdateSlotDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UpdateSlotDTO();
        dto.setDoctorId("D1");
        dto.setDate(LocalDate.of(2025, 10, 28));
        dto.setStartTime(LocalTime.of(9, 0, 0));
        dto.setAvailable(true);
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFieldsCorrectly() {
        assertEquals("D1", dto.getDoctorId());
        assertEquals(LocalDate.of(2025, 10, 28), dto.getDate());
        assertEquals(LocalTime.of(9, 0, 0), dto.getStartTime());
        assertTrue(dto.isAvailable());
    }

    @Test
    @DisplayName("DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        UpdateSlotDTO dto2 = new UpdateSlotDTO();
        dto2.setDoctorId("D1");
        dto2.setDate(LocalDate.of(2025, 10, 28));
        dto2.setStartTime(LocalTime.of(9, 0, 0));
        dto2.setAvailable(true);

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("DTOs with different data should not be equal")
    void dto_equalsShouldReturnFalseForDifferentData() {
        UpdateSlotDTO dto2 = new UpdateSlotDTO();
        dto2.setDoctorId("DIFFERENT");

        assertNotEquals(dto, dto2);
    }

    @Test
    @DisplayName("toString should return a non-empty string")
    void dto_toStringShouldReturnValidString() {
        String result = dto.toString();
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}
