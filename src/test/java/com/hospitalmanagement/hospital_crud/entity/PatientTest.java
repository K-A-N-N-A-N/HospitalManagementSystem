package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    private Validator validator;

    Patient patient;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        patient = new Patient();
        patient.setId("P1");
        patient.setName("Jane Doe");
        patient.setAge(30);
        patient.setGender("Female");
        patient.setAddress("123 Main St");
        patient.setActive(true);
    }

    @Test
    @DisplayName("Should correctly set and get all patient fields")
    void patientEntity_shouldSetAndGetFields() {
        Instant now = Instant.now();
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);
        patient.setEmail("jane@example.com");
        patient.setPhoneNumber("+911234567890");

        assertEquals("P1", patient.getId());
        assertEquals("Jane Doe", patient.getName());
        assertEquals(30, patient.getAge());
        assertEquals("Female", patient.getGender());
        assertEquals("123 Main St", patient.getAddress());
        assertTrue(patient.getActive());
        assertEquals("jane@example.com", patient.getEmail());
        assertEquals("+911234567890", patient.getPhoneNumber());
        assertEquals(now, patient.getCreatedAt());
        assertEquals(now, patient.getUpdatedAt());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void patientEntity_shouldFailValidationForInvalidEmail() {
        patient.setEmail("invalid-email");
        patient.setPhoneNumber("9876543210");

        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void patientEntity_shouldFailValidationForInvalidPhone() {
        patient.setEmail("jane@example.com");
        patient.setPhoneNumber("12345"); // invalid

        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"))
        );
    }

    @Test
    @DisplayName("Should have active set to TRUE by default")
    void patientEntity_activeShouldBeTrueByDefault() {
        Patient newPatient = new Patient();
        assertTrue(newPatient.getActive());
    }

    @Test
    @DisplayName("Should pass validation when email and phone are valid")
    void patientEntity_shouldPassValidationForValidFields() {
        patient.setEmail("jane@example.com");
        patient.setPhoneNumber("+911234567890");

        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Patient entities should match by ID")
    void patientEntity_shouldMatchById() {
        Patient p2 = new Patient();
        p2.setId("P1");

        assertEquals(patient.getId(), p2.getId());
    }
}