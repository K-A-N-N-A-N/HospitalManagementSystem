package com.hospitalmanagement.hospital_crud.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentNotification(String doctorEmail, String doctorName, String patientName, String appointmentTime) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(doctorEmail);
            message.setSubject("[EMAIL] New Appointment Scheduled");
            message.setText(String.format(
                    "Hello Dr. %s,\n\nA new appointment has been booked with patient %s.\nAppointment Time: %s\n\nBest regards,\nHospital Management System",
                    doctorName, patientName, appointmentTime
            ));
            mailSender.send(message);
            log.info("[EMAIL] Email sent successfully to {}", doctorEmail);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send email to {}: {}", doctorEmail, e.getMessage());
        }
    }
}
