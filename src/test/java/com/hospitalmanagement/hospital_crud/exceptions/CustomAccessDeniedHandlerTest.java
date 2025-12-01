package com.hospitalmanagement.hospital_crud.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    CustomAccessDeniedHandler handler;
    HttpServletRequest request;
    HttpServletResponse response;
    AccessDeniedException accessDeniedException;

    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        handler = new CustomAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        accessDeniedException = new AccessDeniedException("Forbidden");

        outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {outputStream.write(b);
            }

            @Override
            public boolean isReady() {return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    @DisplayName("Should return 403 Forbidden with proper JSON response")
    void handle_shouldReturn403WithJsonBody() throws IOException {

        handler.handle(request, response, accessDeniedException);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response, times(1)).setContentType("application/json");

        Map body = new ObjectMapper().readValue(outputStream.toByteArray(), Map.class);

        assertEquals(403, body.get("status"));
        assertEquals("Forbidden", body.get("error"));
        assertEquals("You do not have access to this resource", body.get("message"));
    }
}
