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

class PatientVisitHistoryTest {

    private Validator validator;
    PatientVisitHistory history;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        history = new PatientVisitHistory();
        history.setId("PVH1");
        history.setPatientId("P1");
        history.setPatientName("Jane Doe");
        history.setAppointmentId("A1");
    }

    @Test
    @DisplayName("Should correctly set and get all patient visit history fields")
    void patientVisitHistory_shouldSetAndGetFields() {
        Instant now = Instant.now();
        history.setCreatedAt(now);
        history.setUpdatedAt(now);
        history.setVisitDate(LocalDateTime.of(2025, 10, 28, 9, 30));
        history.setAppointmentSummaryJson("{\"summary\":\"ok\"}");

        assertEquals("PVH1", history.getId());
        assertEquals("P1", history.getPatientId());
        assertEquals("Jane Doe", history.getPatientName());
        assertEquals("A1", history.getAppointmentId());
        assertEquals(LocalDateTime.of(2025, 10, 28, 9, 30), history.getVisitDate());
        assertEquals("{\"summary\":\"ok\"}", history.getAppointmentSummaryJson());
        assertEquals(now, history.getCreatedAt());
        assertEquals(now, history.getUpdatedAt());
    }
}