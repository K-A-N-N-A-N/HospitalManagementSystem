package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    private Validator validator;
    Prescription prescription;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        prescription = new Prescription();
        prescription.setId("PR1");
    }

    @Test
    @DisplayName("Should correctly set and get all prescription fields")
    void prescription_shouldSetAndGetFields() {
        Instant now = Instant.now();
        prescription.setCreatedAt(now);
        prescription.setUpdatedAt(now);

        Appointment appt = new Appointment();
        appt.setId("A1");
        prescription.setAppointment(appt);

        assertEquals("PR1", prescription.getId());
        assertEquals("A1", prescription.getAppointment().getId());
        assertNotNull(prescription.getMedicines());
        assertTrue(prescription.getMedicines().isEmpty());
        assertEquals(now, prescription.getCreatedAt());
        assertEquals(now, prescription.getUpdatedAt());
    }
}