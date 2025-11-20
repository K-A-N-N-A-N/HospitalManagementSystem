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

class DoctorTest {

    private Validator validator;

    Doctor doctor;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("John Doe");
        doctor.setSpecialization("Cardio");
        doctor.setPhotoPath("/photos/doc1.jpg");
        doctor.setActive(true);
    }

    @Test
    @DisplayName("Should correctly set and get all doctor fields")
    void doctorEntity_shouldSetAndGetFields() {
        Instant now = Instant.now();
        doctor.setCreatedAt(now);
        doctor.setUpdatedAt(now);
        doctor.setEmail("john@example.com");
        doctor.setPhoneNumber("+911234567890");

        assertEquals("D1", doctor.getId());
        assertEquals("John Doe", doctor.getName());
        assertEquals("Cardio", doctor.getSpecialization());
        assertEquals("/photos/doc1.jpg", doctor.getPhotoPath());
        assertTrue(doctor.getActive());
        assertEquals("john@example.com", doctor.getEmail());
        assertEquals("+911234567890", doctor.getPhoneNumber());
        assertEquals(now, doctor.getCreatedAt());
        assertEquals(now, doctor.getUpdatedAt());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void doctorEntity_shouldFailValidationForInvalidEmail() {
        doctor.setEmail("invalid-email");
        doctor.setPhoneNumber("9876543210");

        Set<ConstraintViolation<Doctor>> violations = validator.validate(doctor);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void doctorEntity_shouldFailValidationForInvalidPhone() {
        doctor.setEmail("john@example.com");
        doctor.setPhoneNumber("12345"); // invalid

        Set<ConstraintViolation<Doctor>> violations = validator.validate(doctor);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"))
        );
    }

    @Test
    @DisplayName("Should have active set to TRUE by default")
    void doctorEntity_activeShouldBeTrueByDefault() {
        Doctor newDoctor = new Doctor();
        assertTrue(newDoctor.getActive());
    }

    @Test
    @DisplayName("Should pass validation when email and phone are valid")
    void doctorEntity_shouldPassValidationForValidFields() {
        doctor.setEmail("john@example.com");
        doctor.setPhoneNumber("+911234567890");

        Set<ConstraintViolation<Doctor>> violations = validator.validate(doctor);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Doctor entities should match by ID")
    void doctorEntity_shouldMatchById() {
        Doctor d2 = new Doctor();
        d2.setId("D1");

        assertEquals(doctor.getId(), d2.getId());
    }
}
