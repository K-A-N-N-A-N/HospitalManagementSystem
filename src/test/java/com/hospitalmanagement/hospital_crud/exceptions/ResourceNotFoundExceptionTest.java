package com.hospitalmanagement.hospital_crud.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should store correct message in ResourceNotFoundException")
    void shouldStoreCorrectMessage() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Doctor not found");

        assertEquals("Doctor not found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldBeRuntimeException() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Any message");

        assertTrue(ex instanceof RuntimeException);
    }
}
