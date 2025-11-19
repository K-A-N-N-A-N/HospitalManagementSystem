package com.hospitalmanagement.hospital_crud.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    private Validator validator;
    AuditLog log;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        log = new AuditLog();
        log.setId("AL1");
        log.setEntityName("Patient");
        log.setEntityId("P1");
        log.setAction("CREATE");
        log.setPerformedBy("admin");
        log.setRole("ADMIN");
        log.setChanges("{\"name\":\"Jane\"}");
    }

    @Test
    @DisplayName("Should correctly set and get all audit log fields")
    void auditLog_shouldSetAndGetFields() {
        Instant now = Instant.now();
        log.setCreatedAt(now);

        assertEquals("AL1", log.getId());
        assertEquals("Patient", log.getEntityName());
        assertEquals("P1", log.getEntityId());
        assertEquals("CREATE", log.getAction());
        assertEquals("admin", log.getPerformedBy());
        assertEquals("ADMIN", log.getRole());
        assertEquals("{\"name\":\"Jane\"}", log.getChanges());
        assertEquals(now, log.getCreatedAt());
    }
}