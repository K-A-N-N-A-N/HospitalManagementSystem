package com.hospitalmanagement.hospital_crud.dto;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DoctorSlotResponseTest {

    DoctorSlotResponse resp;

    @BeforeEach
    void setUp() {
        Doctor doctor = new Doctor();
        doctor.setId("D1");

        DoctorSlot slot = DoctorSlot.builder()
                .id("DS1")
                .doctor(doctor)
                .date(LocalDate.of(2025, 10, 28))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .available(true)
                .build();

        resp = new DoctorSlotResponse(slot);
    }

    @Test
    @DisplayName("Should correctly map entity to response DTO")
    void dto_shouldMapFromEntity() {
        assertEquals("DS1", resp.getId());
        assertEquals("D1", resp.getDoctorId());
        assertEquals(LocalDate.of(2025, 10, 28), resp.getDate());
        assertEquals(LocalTime.of(9, 0), resp.getStartTime());
        assertEquals(LocalTime.of(9, 30), resp.getEndTime());
        assertTrue(resp.isAvailable());
    }

    @Test
    @DisplayName("toString method should not be null or empty")
    void dto_toStringShouldReturnValidString() {
        String result = resp.toString();
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}