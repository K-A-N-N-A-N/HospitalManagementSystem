package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionItemDTOTest {

    PrescriptionItemDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PrescriptionItemDTO();
        dto.setId("PI1");
        dto.setMedicineName("Ibuprofen");
        dto.setDosage("200mg");
        dto.setNotes("Twice a day");
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("PI1", dto.getId());
        assertEquals("Ibuprofen", dto.getMedicineName());
        assertEquals("200mg", dto.getDosage());
        assertEquals("Twice a day", dto.getNotes());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        PrescriptionItemDTO dto2 = new PrescriptionItemDTO("PI1", "Ibuprofen", "200mg", "Twice a day");
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