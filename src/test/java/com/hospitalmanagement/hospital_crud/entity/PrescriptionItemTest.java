package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionItemTest {

    private Validator validator;
    PrescriptionItem item;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        item = new PrescriptionItem();
        item.setId("PI1");
        item.setMedicineName("Paracetamol");
        item.setDosage("500mg");
        item.setNotes("After food");
    }

    @Test
    @DisplayName("Should correctly set and get all prescription item fields")
    void prescriptionItem_shouldSetAndGetFields() {
        Prescription p = new Prescription();
        p.setId("PR1");
        item.setPrescription(p);

        assertEquals("PI1", item.getId());
        assertEquals("Paracetamol", item.getMedicineName());
        assertEquals("500mg", item.getDosage());
        assertEquals("After food", item.getNotes());
        assertEquals("PR1", item.getPrescription().getId());
    }
}