package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentSummaryServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    PrescriptionRepository prescriptionRepository;

    @InjectMocks
    AppointmentSummaryService appointmentSummaryService;

    Appointment completedAppointment;
    Doctor doctor;
    Patient patient;
    Prescription prescription;
    PrescriptionItem prescriptionItem;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. House");

        patient = new Patient();
        patient.setId("P1");
        patient.setName("John Doe");

        completedAppointment = new Appointment();
        completedAppointment.setId("A1");
        completedAppointment.setDoctor(doctor);
        completedAppointment.setPatient(patient);
        completedAppointment.setReason("Routine Checkup");
        completedAppointment.setAppointmentTime(LocalDateTime.of(2025, 11, 20, 10, 0));
        completedAppointment.setStatus(AppointmentStatus.COMPLETED);

        prescriptionItem = new PrescriptionItem();
        prescriptionItem.setId("PI1");
        prescriptionItem.setMedicineName("Paracetamol");
        prescriptionItem.setDosage("500mg");
        prescriptionItem.setNotes("Twice a day");

        prescription = new Prescription();
        prescription.setId("PR1");
        prescription.setAppointment(completedAppointment);
        prescription.setMedicines(List.of(prescriptionItem));
    }

    @Test
    @DisplayName("Should return AppointmentSummaryDTO when appointment completed and prescription exists")
    void getAppointmentSummary_shouldReturnSummary_whenCompletedWithPrescription() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(completedAppointment));
        when(prescriptionRepository.findByAppointmentId("A1")).thenReturn(Optional.of(prescription));

        Object result = appointmentSummaryService.getAppointmentSummary("A1");

        assertTrue(result instanceof AppointmentSummaryDTO);
        AppointmentSummaryDTO summaryDTO = (AppointmentSummaryDTO) result;
        assertEquals("A1", summaryDTO.getAppointmentId());
        assertEquals("D1", summaryDTO.getDoctorId());
        assertEquals("P1", summaryDTO.getPatientId());
        assertEquals("PR1", summaryDTO.getPrescriptionId());
        assertEquals(1, summaryDTO.getMedicines().size());
        PrescriptionItemDTO itemDTO = summaryDTO.getMedicines().get(0);
        assertEquals("Paracetamol", itemDTO.getMedicineName());

        verify(appointmentRepository, times(1)).findById("A1");
        verify(prescriptionRepository, times(1)).findByAppointmentId("A1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void getAppointmentSummary_shouldThrowException_whenAppointmentNotFound() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentSummaryService.getAppointmentSummary("A1"));

        verify(appointmentRepository, times(1)).findById("A1");
        verifyNoInteractions(prescriptionRepository);
    }

    @Test
    @DisplayName("Should return response message when appointment not completed")
    void getAppointmentSummary_shouldReturnMessage_whenAppointmentNotCompleted() {
        completedAppointment.setStatus(AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(completedAppointment));

        Object result = appointmentSummaryService.getAppointmentSummary("A1");

        assertTrue(result instanceof AppointmentSummaryService.ResponseMessage);
        AppointmentSummaryService.ResponseMessage message = (AppointmentSummaryService.ResponseMessage) result;
        assertEquals("Appointment is not completed yet. Prescription will be available after completion.", message.getMessage());

        verify(appointmentRepository, times(1)).findById("A1");
        verifyNoInteractions(prescriptionRepository);
    }

    @Test
    @DisplayName("Should return response message when no prescription exists for completed appointment")
    void getAppointmentSummary_shouldReturnMessage_whenNoPrescriptionFound() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(completedAppointment));
        when(prescriptionRepository.findByAppointmentId("A1")).thenReturn(Optional.empty());

        Object result = appointmentSummaryService.getAppointmentSummary("A1");

        assertTrue(result instanceof AppointmentSummaryService.ResponseMessage);
        AppointmentSummaryService.ResponseMessage message = (AppointmentSummaryService.ResponseMessage) result;
        assertEquals("No prescription found for this completed appointment.", message.getMessage());

        verify(appointmentRepository, times(1)).findById("A1");
        verify(prescriptionRepository, times(1)).findByAppointmentId("A1");
    }
}

