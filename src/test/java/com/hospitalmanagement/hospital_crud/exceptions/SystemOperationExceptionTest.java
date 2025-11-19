package com.hospitalmanagement.hospital_crud.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemOperationExceptionTest {

    @Test
    @DisplayName("Should store correct message in SystemOperationException")
    void shouldStoreCorrectMessage() {
        SystemOperationException ex =
                new SystemOperationException("Email service failed");

        assertEquals("Email service failed", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should store correct message and cause in SystemOperationException")
    void shouldStoreCorrectMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        SystemOperationException ex =
                new SystemOperationException("Failed operation", cause);

        assertEquals("Failed operation", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeRuntimeException() {
        SystemOperationException ex =
                new SystemOperationException("Any message");

        assertTrue(ex instanceof RuntimeException);
    }
}
