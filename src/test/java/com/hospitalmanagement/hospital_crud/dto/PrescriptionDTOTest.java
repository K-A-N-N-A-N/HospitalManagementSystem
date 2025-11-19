package com.hospitalmanagement.hospital_crud.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionDTOTest {

    PrescriptionDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PrescriptionDTO();
        dto.setId("PR1");
        dto.setAppointment_id("A1");
        dto.setMedicines(List.of(new PrescriptionItemDTO("PI1", "Paracetamol", "500mg", "After food")));
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
    }

    @Test
    @DisplayName("Should correctly set and get all fields")
    void dto_shouldSetAndGetFields() {
        assertEquals("PR1", dto.getId());
        assertEquals("A1", dto.getAppointment_id());
        assertNotNull(dto.getMedicines());
        assertEquals(1, dto.getMedicines().size());
        assertEquals("PI1", dto.getMedicines().get(0).getId());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal (Lombok @Data)")
    void dto_equalsShouldReturnTrueForSameData() {
        PrescriptionDTO dto2 = new PrescriptionDTO();
        dto2.setId(dto.getId());
        dto2.setAppointment_id(dto.getAppointment_id());
        dto2.setMedicines(dto.getMedicines());
        dto2.setCreatedAt(dto.getCreatedAt());
        dto2.setUpdatedAt(dto.getUpdatedAt());

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