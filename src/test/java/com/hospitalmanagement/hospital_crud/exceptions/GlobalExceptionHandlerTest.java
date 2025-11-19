package com.hospitalmanagement.hospital_crud.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should return 404 for ResourceNotFoundException")
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Doctor not found");

        ResponseEntity<String> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Doctor not found", response.getBody());
    }

    @Test
    @DisplayName("Should return 400 and validation messages for MethodArgumentNotValidException")
    void handleValidationExceptions_shouldReturn400() {

        FieldError fieldError1 = new FieldError("object", "email", "Invalid email");
        FieldError fieldError2 = new FieldError("object", "phoneNumber", "Invalid phone number");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid email", body.get("email"));
        assertEquals("Invalid phone number", body.get("phoneNumber"));
    }

    @Test
    @DisplayName("Should return 500 for JsonProcessingException")
    void handleJsonProcessingException_shouldReturn500() {
        JsonProcessingException ex = mock(JsonProcessingException.class);
        when(ex.getOriginalMessage()).thenReturn("JSON malformed");

        ResponseEntity<String> response = handler.handleJsonProcessingException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("JSON processing error: JSON malformed", response.getBody());
    }

    @Test
    @DisplayName("Should return 400 for IllegalArgumentException")
    void handleIllegalArgument_shouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid ID format");

        ResponseEntity<String> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data: Invalid ID format", response.getBody());
    }

    @Test
    @DisplayName("Should return 503 for SystemOperationException")
    void handleSystemOperationException_shouldReturn503() {
        SystemOperationException ex = new SystemOperationException("Email service unreachable");

        ResponseEntity<String> response = handler.handleSystemOperationException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("System operation failed: Email service unreachable", response.getBody());
    }

    @Test
    @DisplayName("Should return 500 for generic exceptions")
    void handleGenericException_shouldReturn500() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<String> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Something went wrong", response.getBody());
    }
}
