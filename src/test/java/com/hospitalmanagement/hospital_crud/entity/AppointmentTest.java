package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Validator validator;
    Appointment appointment;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        appointment = new Appointment();
        appointment.setId("A1");
    }

    @Test
    @DisplayName("Should correctly set and get all appointment fields")
    void appointment_shouldSetAndGetFields() {
        Instant now = Instant.now();
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        Patient p = new Patient();
        p.setId("P1");
        appointment.setPatient(p);

        Doctor d = new Doctor();
        d.setId("D1");
        appointment.setDoctor(d);

        appointment.setAppointmentTime(LocalDateTime.of(2025, 10, 28, 9, 30));
        appointment.setReason("Regular checkup");

        DoctorSlot slot = new DoctorSlot();
        slot.setId("DS1");
        appointment.setSlot(slot);

        assertEquals("A1", appointment.getId());
        assertEquals("P1", appointment.getPatient().getId());
        assertEquals("D1", appointment.getDoctor().getId());
        assertEquals(LocalDateTime.of(2025, 10, 28, 9, 30), appointment.getAppointmentTime());
        assertEquals("Regular checkup", appointment.getReason());
        assertEquals("DS1", appointment.getSlot().getId());
        assertEquals(now, appointment.getCreatedAt());
        assertEquals(now, appointment.getUpdatedAt());
    }
}