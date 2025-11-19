package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DoctorSlotTest {

    private Validator validator;
    DoctorSlot slot;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        slot = new DoctorSlot();
        slot.setId("DS1");
    }

    @Test
    @DisplayName("Should correctly set and get all doctor slot fields")
    void doctorSlot_shouldSetAndGetFields() {
        Doctor doctor = new Doctor();
        doctor.setId("D1");
        slot.setDoctor(doctor);
        slot.setDate(LocalDate.of(2025, 10, 28));
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));

        assertEquals("DS1", slot.getId());
        assertEquals("D1", slot.getDoctor().getId());
        assertEquals(LocalDate.of(2025, 10, 28), slot.getDate());
        assertEquals(LocalTime.of(9, 0), slot.getStartTime());
        assertEquals(LocalTime.of(10, 0), slot.getEndTime());
        assertTrue(slot.isAvailable());
    }
}