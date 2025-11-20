package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SlotRequestDTOTest {

    SlotRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new SlotRequestDTO();
        dto.setDoctorId("D1");
        dto.setDate(LocalDate.of(2025, 10, 28));
        dto.setDurationMinutes(30);
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("D1", dto.getDoctorId());
        assertEquals(LocalDate.of(2025, 10, 28), dto.getDate());
        assertEquals(30, dto.getDurationMinutes());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        SlotRequestDTO dto2 = new SlotRequestDTO();
        dto2.setDoctorId("D1");
        dto2.setDate(LocalDate.of(2025, 10, 28));
        dto2.setDurationMinutes(30);

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("toString method should not be null or empty")
    void dto_toStringShouldReturnValidString() {
        String result = dto.toString();
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}