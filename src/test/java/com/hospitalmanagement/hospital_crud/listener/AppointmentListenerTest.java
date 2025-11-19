package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.dto.AppointmentMessageDTO;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import com.hospitalmanagement.hospital_crud.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentListenerTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    DoctorSlotRepository slotRepository;

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    PatientRepository patientRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    AppointmentListener listener;

    Doctor doctor;
    Patient patient;
    DoctorSlot slot;
    AppointmentMessageDTO msg;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. John");
        doctor.setEmail("doctor@mail.com");

        patient = new Patient();
        patient.setId("P1");
        patient.setName("Kannan");

        slot = new DoctorSlot();
        slot.setId("S1");

        msg = new AppointmentMessageDTO();
        msg.setDoctorId("D1");
        msg.setPatientId("P1");
        msg.setSlotId("S1");
        msg.setAppointmentTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        msg.setReason("Consultation");
    }

    @Test
    @DisplayName("Should create appointment and send email when valid message is received")
    void handleAppointmentCreate_shouldCreateAppointmentAndSendEmail() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient));
        when(slotRepository.findById("S1")).thenReturn(Optional.of(slot));

        listener.handleAppointmentCreate(msg);

        verify(doctorRepository).findById("D1");
        verify(patientRepository).findById("P1");
        verify(slotRepository).findById("S1");

        verify(appointmentRepository, times(1)).save(any(Appointment.class));

        verify(emailService, times(1)).sendAppointmentNotification(
                eq("doctor@mail.com"),
                eq("Dr. John"),
                eq("Kannan"),
                eq("2025-01-01T10:30")
        );
    }

    @Test
    @DisplayName("Should log error and not crash when doctor is missing")
    void handleAppointmentCreate_shouldHandleMissingDoctorGracefully() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        listener.handleAppointmentCreate(msg);

        verify(doctorRepository).findById("D1");
        verify(patientRepository, never()).findById(anyString());
        verify(slotRepository, never()).findById(anyString());
        verify(appointmentRepository, never()).save(any());
        verify(emailService, never()).sendAppointmentNotification(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should log error and not crash when patient is missing")
    void handleAppointmentCreate_shouldHandleMissingPatientGracefully() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(patientRepository.findById("P1")).thenReturn(Optional.empty());

        listener.handleAppointmentCreate(msg);

        verify(patientRepository).findById("P1");
        verify(slotRepository, never()).findById(anyString());
        verify(appointmentRepository, never()).save(any());
        verify(emailService, never()).sendAppointmentNotification(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should log error and not crash when slot is missing")
    void handleAppointmentCreate_shouldHandleMissingSlotGracefully() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient));
        when(slotRepository.findById("S1")).thenReturn(Optional.empty());

        listener.handleAppointmentCreate(msg);

        verify(slotRepository).findById("S1");
        verify(appointmentRepository, never()).save(any());
        verify(emailService, never()).sendAppointmentNotification(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should log error and not crash if saving appointment fails")
    void handleAppointmentCreate_shouldHandleSaveFailureGracefully() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient));
        when(slotRepository.findById("S1")).thenReturn(Optional.of(slot));

        doThrow(new RuntimeException("Save failed!"))
                .when(appointmentRepository).save(any(Appointment.class));

        listener.handleAppointmentCreate(msg);

        verify(emailService, never()).sendAppointmentNotification(any(), any(), any(), any());
    }
}
