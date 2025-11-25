package com.hospitalmanagement.hospital_crud.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    CustomAuthenticationEntryPoint entryPoint;
    HttpServletRequest request;
    HttpServletResponse response;
    AuthenticationException authException;

    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        entryPoint = new CustomAuthenticationEntryPoint();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authException = mock(AuthenticationException.class);

        outputStream = new ByteArrayOutputStream();

        // Fixed ServletOutputStream with required methods
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // not needed
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized with proper JSON response")
    void commence_shouldReturn401WithJsonBody() throws IOException {

        entryPoint.commence(request, response, authException);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");

        Map body = new ObjectMapper().readValue(outputStream.toByteArray(), Map.class);

        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Session expired or invalid token. Please login again.", body.get("message"));
    }
}
