package com.hospitalmanagement.hospital_crud.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    EmailService emailService;

    @Test
    @DisplayName("Should send appointment notification email with correct details")
    void sendAppointmentNotification_shouldSendEmail_withCorrectContent() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendAppointmentNotification(
                "doctor@example.com",
                "House",
                "John Doe",
                "2025-11-20 10:00"
        );

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertNotNull(message);
        assertEquals("[EMAIL] New Appointment Scheduled", message.getSubject());
        assertEquals("doctor@example.com", message.getTo()[0]);
        String body = message.getText();
        assertNotNull(body);
        assertEquals(true, body.contains("Dr. House"));
        assertEquals(true, body.contains("patient John Doe"));
        assertEquals(true, body.contains("2025-11-20 10:00"));
    }

    @Test
    @DisplayName("Should handle exception when mail sender fails")
    void sendAppointmentNotification_shouldHandleException_whenMailSenderFails() {
        doThrow(new RuntimeException("Mail server down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Ensure no exception bubbles up
        emailService.sendAppointmentNotification(
                "doctor@example.com",
                "House",
                "John Doe",
                "2025-11-20 10:00"
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

