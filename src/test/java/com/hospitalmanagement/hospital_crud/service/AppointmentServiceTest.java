package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentMessageDTO;
import com.hospitalmanagement.hospital_crud.dto.AppointmentRequest;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    PatientRepository patientRepository;

    @Mock
    DoctorSlotRepository slotRepository;

    @Mock
    QueueService queueService;

    @InjectMocks
    AppointmentService appointmentService;

    Doctor doctor1;
    Patient patient1;
    DoctorSlot slot1;
    Appointment appointment1;
    Appointment appointment2;
    AppointmentRequest appointmentRequest;

    @BeforeEach
    void setUp() {
        // Setup Doctor
        doctor1 = new Doctor();
        doctor1.setId("D1");
        doctor1.setName("Dr. Smith");
        doctor1.setActive(true);

        // Setup Patient
        patient1 = new Patient();
        patient1.setId("P1");
        patient1.setName("John Doe");
        patient1.setActive(true);

        // Setup DoctorSlot
        slot1 = new DoctorSlot();
        slot1.setId("S1");
        slot1.setDoctor(doctor1);
        slot1.setDate(LocalDate.of(2025, 11, 20));
        slot1.setStartTime(LocalTime.of(9, 0));
        slot1.setEndTime(LocalTime.of(10, 0));
        slot1.setAvailable(true);

        // Setup Appointment
        appointment1 = new Appointment();
        appointment1.setId("A1");
        appointment1.setDoctor(doctor1);
        appointment1.setPatient(patient1);
        appointment1.setSlot(slot1);
        appointment1.setAppointmentTime(LocalDateTime.of(2025, 11, 20, 9, 30));
        appointment1.setReason("Regular checkup");
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        appointment2 = new Appointment();
        appointment2.setId("A2");
        appointment2.setDoctor(doctor1);
        appointment2.setPatient(patient1);
        appointment2.setStatus(AppointmentStatus.COMPLETED);

        // Setup AppointmentRequest
        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setDoctorId("D1");
        appointmentRequest.setPatientId("P1");
        appointmentRequest.setAppointmentTime(LocalDateTime.of(2025, 11, 20, 9, 30));
        appointmentRequest.setReason("Regular checkup");
    }

    @Test
    @DisplayName("Should create appointment successfully when all conditions are met")
    void createAppointment_shouldCreateAppointment_whenAllConditionsMet() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor1));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient1));
        when(slotRepository.findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30))))
                .thenReturn(Optional.of(slot1));
        when(slotRepository.save(any(DoctorSlot.class))).thenReturn(slot1);
        doNothing().when(queueService).sendToQueue(anyString(), any(AppointmentMessageDTO.class));

        Appointment result = appointmentService.createAppointment(appointmentRequest);

        assertNotNull(result);
        assertEquals(doctor1, result.getDoctor());
        assertEquals(patient1, result.getPatient());
        assertEquals(slot1, result.getSlot());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        assertFalse(slot1.isAvailable());

        verify(doctorRepository, times(1)).findById("D1");
        verify(patientRepository, times(1)).findById("P1");
        verify(slotRepository, times(1)).findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30)));
        verify(slotRepository, times(1)).save(slot1);
        verify(queueService, times(1)).sendToQueue(eq("appointment.queue"), any(AppointmentMessageDTO.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor not found")
    void createAppointment_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentRequest)
        );

        verify(doctorRepository, times(1)).findById("D1");
        verify(patientRepository, never()).findById(anyString());
        verify(slotRepository, never()).findAvailableSlotForDoctorAtTime(anyString(), any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient not found")
    void createAppointment_shouldThrowException_whenPatientNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor1));
        when(patientRepository.findById("P1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentRequest)
        );

        verify(doctorRepository, times(1)).findById("D1");
        verify(patientRepository, times(1)).findById("P1");
        verify(slotRepository, never()).findAvailableSlotForDoctorAtTime(anyString(), any(), any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when no available slot found")
    void createAppointment_shouldThrowException_whenNoSlotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor1));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient1));
        when(slotRepository.findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30))))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(appointmentRequest)
        );

        verify(doctorRepository, times(1)).findById("D1");
        verify(patientRepository, times(1)).findById("P1");
        verify(slotRepository, times(1)).findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30)));
    }

    @Test
    @DisplayName("Should throw RuntimeException when slot is not available")
    void createAppointment_shouldThrowException_whenSlotNotAvailable() {
        slot1.setAvailable(false);

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor1));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient1));
        when(slotRepository.findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30))))
                .thenReturn(Optional.of(slot1));

        assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(appointmentRequest)
        );

        verify(slotRepository, never()).save(any(DoctorSlot.class));
    }

    @Test
    @DisplayName("Should revert slot availability when queue send fails")
    void createAppointment_shouldRevertSlot_whenQueueSendFails() {
        slot1.setAvailable(true);

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor1));
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient1));
        when(slotRepository.findAvailableSlotForDoctorAtTime(
                eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 30))))
                .thenReturn(Optional.of(slot1));
        when(slotRepository.save(any(DoctorSlot.class))).thenReturn(slot1);
        doThrow(new RuntimeException("Queue error")).when(queueService)
                .sendToQueue(anyString(), any(AppointmentMessageDTO.class));

        assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(appointmentRequest)
        );

        assertTrue(slot1.isAvailable());
        verify(slotRepository, times(2)).save(slot1); // Once to set unavailable, once to revert
    }

    @Test
    @DisplayName("Should update appointment with all fields")
    void updateAppointment_shouldUpdateAllFields_whenAllFieldsProvided() {
        AppointmentRequest updateRequest = new AppointmentRequest();
        updateRequest.setDoctorId("D2");
        updateRequest.setPatientId("P2");
        updateRequest.setAppointmentTime(LocalDateTime.of(2025, 11, 21, 10, 0));
        updateRequest.setReason("Updated reason");

        Doctor doctor2 = new Doctor();
        doctor2.setId("D2");
        doctor2.setName("Dr. Jones");

        Patient patient2 = new Patient();
        patient2.setId("P2");
        patient2.setName("Jane Doe");

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(doctorRepository.findById("D2")).thenReturn(Optional.of(doctor2));
        when(patientRepository.findById("P2")).thenReturn(Optional.of(patient2));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment1);

        Appointment result = appointmentService.updateAppointment("A1", updateRequest);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById("A1");
        verify(doctorRepository, times(1)).findById("D2");
        verify(patientRepository, times(1)).findById("P2");
        verify(appointmentRepository, times(1)).save(appointment1);
    }

    @Test
    @DisplayName("Should update appointment with partial fields (null values ignored)")
    void updateAppointment_shouldUpdatePartialFields_whenSomeFieldsNull() {
        AppointmentRequest updateRequest = new AppointmentRequest();
        updateRequest.setDoctorId(null);
        updateRequest.setPatientId(null);
        updateRequest.setAppointmentTime(LocalDateTime.of(2025, 11, 21, 10, 0));
        updateRequest.setReason("Updated reason");

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment1);

        Appointment result = appointmentService.updateAppointment("A1", updateRequest);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById("A1");
        verify(doctorRepository, never()).findById(anyString());
        verify(patientRepository, never()).findById(anyString());
        verify(appointmentRepository, times(1)).save(appointment1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found during update")
    void updateAppointment_shouldThrowException_whenAppointmentNotFound() {
        AppointmentRequest updateRequest = new AppointmentRequest();

        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateAppointment("A1", updateRequest)
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor not found during update")
    void updateAppointment_shouldThrowException_whenDoctorNotFound() {
        AppointmentRequest updateRequest = new AppointmentRequest();
        updateRequest.setDoctorId("D2");

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(doctorRepository.findById("D2")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateAppointment("A1", updateRequest)
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(doctorRepository, times(1)).findById("D2");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient not found during update")
    void updateAppointment_shouldThrowException_whenPatientNotFound() {
        AppointmentRequest updateRequest = new AppointmentRequest();
        updateRequest.setPatientId("P2");

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(patientRepository.findById("P2")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateAppointment("A1", updateRequest)
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(patientRepository, times(1)).findById("P2");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should return list of all appointments")
    void getAllAppointments_shouldReturnAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        List<Appointment> result = appointmentService.getAllAppointments();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return list of active appointments")
    void getAllActiveAppointments_shouldReturnActiveAppointments() {
        List<AppointmentStatus> activeStatuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED);
        when(appointmentRepository.findByStatusIn(activeStatuses))
                .thenReturn(List.of(appointment1, appointment2));

        List<Appointment> result = appointmentService.getAllActiveAppointments();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository, times(1)).findByStatusIn(activeStatuses);
    }

    @Test
    @DisplayName("Should return appointment when appointment exists and not cancelled")
    void getAppointmentById_shouldReturnAppointment_whenExistsAndNotCancelled() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));

        Appointment result = appointmentService.getAppointmentById("A1");

        assertNotNull(result);
        assertEquals("A1", result.getId());
        verify(appointmentRepository, times(1)).findById("A1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void getAppointmentById_shouldThrowException_whenAppointmentNotFound() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentById("A1")
        );

        verify(appointmentRepository, times(1)).findById("A1");
    }

    @Test
    @DisplayName("Should throw RuntimeException when appointment is cancelled")
    void getAppointmentById_shouldThrowException_whenAppointmentCancelled() {
        appointment1.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));

        assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentById("A1")
        );

        verify(appointmentRepository, times(1)).findById("A1");
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void cancelAppointment_shouldCancelAppointment_whenAppointmentExists() {
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment1);

        String result = appointmentService.cancelAppointment("A1");

        assertEquals("Appointment has been successfully Cancelled", result);
        assertEquals(AppointmentStatus.CANCELLED, appointment1.getStatus());
        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, times(1)).save(appointment1);
    }

    @Test
    @DisplayName("Should return message when appointment already cancelled")
    void cancelAppointment_shouldReturnMessage_whenAlreadyCancelled() {
        appointment1.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));

        String result = appointmentService.cancelAppointment("A1");

        assertEquals("Appointment Already cancelled", result);
        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found during cancel")
    void cancelAppointment_shouldThrowException_whenAppointmentNotFound() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.cancelAppointment("A1")
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should delete appointment and free slot when slot exists")
    void deleteAppointment_shouldDeleteAndFreeSlot_whenSlotExists() {
        appointment1.setSlot(slot1);
        slot1.setAvailable(false);

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(slotRepository.save(any(DoctorSlot.class))).thenReturn(slot1);
        doNothing().when(appointmentRepository).deleteById("A1");

        appointmentService.deleteAppointment("A1");

        assertTrue(slot1.isAvailable());
        verify(appointmentRepository, times(1)).findById("A1");
        verify(slotRepository, times(1)).save(slot1);
        verify(appointmentRepository, times(1)).deleteById("A1");
    }

    @Test
    @DisplayName("Should delete appointment when slot is null")
    void deleteAppointment_shouldDelete_whenSlotIsNull() {
        appointment1.setSlot(null);

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        doNothing().when(appointmentRepository).deleteById("A1");

        appointmentService.deleteAppointment("A1");

        verify(appointmentRepository, times(1)).findById("A1");
        verify(slotRepository, never()).save(any(DoctorSlot.class));
        verify(appointmentRepository, times(1)).deleteById("A1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found during delete")
    void deleteAppointment_shouldThrowException_whenAppointmentNotFound() {
        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.deleteAppointment("A1")
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, never()).deleteById(anyString());
    }
}

