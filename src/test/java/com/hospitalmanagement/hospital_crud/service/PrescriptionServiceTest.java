package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.AppointmentStatus;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    PrescriptionRepository prescriptionRepository;

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    AppointmentSummaryService appointmentSummaryService;

    @Mock
    PatientVisitHistoryService patientVisitHistoryService;

    @InjectMocks
    PrescriptionService prescriptionService;

    Prescription prescription1;
    Prescription prescription2;
    Appointment appointment1;
    PrescriptionDTO prescriptionDTO1;
    PrescriptionItemDTO itemDTO1;
    PrescriptionItemDTO itemDTO2;
    AppointmentSummaryDTO appointmentSummaryDTO;

    @BeforeEach
    void setUp() {
        // Setup Appointment
        appointment1 = new Appointment();
        appointment1.setId("A1");
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        // Setup PrescriptionItemDTO
        itemDTO1 = new PrescriptionItemDTO();
        itemDTO1.setId("PI1");
        itemDTO1.setMedicineName("Paracetamol");
        itemDTO1.setDosage("500mg");
        itemDTO1.setNotes("Take twice daily");

        itemDTO2 = new PrescriptionItemDTO();
        itemDTO2.setId("PI2");
        itemDTO2.setMedicineName("Amoxicillin");
        itemDTO2.setDosage("250mg");
        itemDTO2.setNotes("Take after meals");

        // Setup PrescriptionDTO
        prescriptionDTO1 = new PrescriptionDTO();
        prescriptionDTO1.setId("PR1");
        prescriptionDTO1.setAppointment_id("A1");
        prescriptionDTO1.setMedicines(List.of(itemDTO1, itemDTO2));
        prescriptionDTO1.setCreatedAt(Instant.now());
        prescriptionDTO1.setUpdatedAt(Instant.now());

        // Setup Prescription Entity
        prescription1 = new Prescription();
        prescription1.setId("PR1");
        prescription1.setAppointment(appointment1);
        prescription1.setMedicines(new ArrayList<>());

        PrescriptionItem item1 = new PrescriptionItem();
        item1.setId("PI1");
        item1.setMedicineName("Paracetamol");
        item1.setDosage("500mg");
        item1.setNotes("Take twice daily");
        item1.setPrescription(prescription1);

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setId("PI2");
        item2.setMedicineName("Amoxicillin");
        item2.setDosage("250mg");
        item2.setNotes("Take after meals");
        item2.setPrescription(prescription1);

        prescription1.setMedicines(new ArrayList<>(List.of(item1, item2)));

        prescription2 = new Prescription();
        prescription2.setId("PR2");
        prescription2.setAppointment(appointment1);

        // Setup AppointmentSummaryDTO
        appointmentSummaryDTO = new AppointmentSummaryDTO();
        appointmentSummaryDTO.setAppointmentId("A1");
        appointmentSummaryDTO.setDoctorId("D1");
        appointmentSummaryDTO.setDoctorName("Dr. Smith");
        appointmentSummaryDTO.setPatientId("P1");
        appointmentSummaryDTO.setPatientName("John Doe");
        appointmentSummaryDTO.setReasonForVisit("Fever");
        appointmentSummaryDTO.setAppointmentDate(LocalDateTime.now());
        appointmentSummaryDTO.setStatus("COMPLETED");
        appointmentSummaryDTO.setPrescriptionId("PR1");
        appointmentSummaryDTO.setMedicines(List.of(itemDTO1, itemDTO2));
    }

    @Test
    @DisplayName("Should return list of all prescriptions as DTOs")
    void getAllPrescriptions_shouldReturnPrescriptionDTOList() {
        when(prescriptionRepository.findAll()).thenReturn(List.of(prescription1, prescription2));

        List<PrescriptionDTO> result = prescriptionService.getAllPrescriptions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(prescriptionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no prescriptions exist")
    void getAllPrescriptions_shouldReturnEmptyList_whenNoPrescriptions() {
        when(prescriptionRepository.findAll()).thenReturn(List.of());

        List<PrescriptionDTO> result = prescriptionService.getAllPrescriptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(prescriptionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return prescription DTO when prescription with given ID exists")
    void getPrescriptionById_shouldReturnPrescriptionDTO_whenPrescriptionExists() {
        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescription1));

        PrescriptionDTO result = prescriptionService.getPrescriptionById("PR1");

        assertNotNull(result);
        assertEquals("PR1", result.getId());
        verify(prescriptionRepository, times(1)).findById("PR1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when prescription not found")
    void getPrescriptionById_shouldThrowException_whenPrescriptionNotFound() {
        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.getPrescriptionById("PR1")
        );

        verify(prescriptionRepository, times(1)).findById("PR1");
    }

    @Test
    @DisplayName("Should create prescription with appointment, update appointment status, and record visit history")
    void createPrescription_shouldCreatePrescriptionAndUpdateAppointment_whenAppointmentIdProvided() {
        PrescriptionDTO dto = prescriptionDTO1;
        Prescription newPrescription = new Prescription();
        newPrescription.setId("PR1");
        newPrescription.setAppointment(appointment1);
        newPrescription.setMedicines(new ArrayList<>());

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(newPrescription);
        when(appointmentSummaryService.getAppointmentSummary("A1")).thenReturn(appointmentSummaryDTO);

        PrescriptionDTO result = prescriptionService.createPrescription(dto);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById("A1");
        verify(appointmentRepository, times(1)).save(appointment1);
        assertEquals(AppointmentStatus.COMPLETED, appointment1.getStatus());
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        verify(appointmentSummaryService, times(1)).getAppointmentSummary("A1");
        verify(patientVisitHistoryService, times(1)).recordPatientVisitHistory(appointmentSummaryDTO);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment_id is null")
    void createPrescription_shouldThrowException_whenAppointmentIdIsNull() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId("PR1");
        dto.setAppointment_id(null);
        dto.setMedicines(List.of(itemDTO1));

        Prescription newPrescription = new Prescription();
        newPrescription.setId("PR1");
        Appointment appointmentWithNullId = new Appointment();
        appointmentWithNullId.setId(null);
        newPrescription.setAppointment(appointmentWithNullId);
        newPrescription.setMedicines(new ArrayList<>());

        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(newPrescription);
        when(appointmentSummaryService.getAppointmentSummary(null))
                .thenThrow(new ResourceNotFoundException("Appointment not found"));

        // The service will try to get appointment summary even when appointment_id is null
        // This causes a ResourceNotFoundException when trying to find appointment with null ID
        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.createPrescription(dto)
        );

        verify(appointmentRepository, never()).findById(anyString());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        verify(appointmentSummaryService, times(1)).getAppointmentSummary(null);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found during creation")
    void createPrescription_shouldThrowException_whenAppointmentNotFound() {
        PrescriptionDTO dto = prescriptionDTO1;

        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.createPrescription(dto)
        );

        verify(appointmentRepository, times(1)).findById("A1");
        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should create prescription with null medicines list")
    void createPrescription_shouldCreatePrescription_whenMedicinesIsNull() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId("PR1");
        dto.setAppointment_id("A1");
        dto.setMedicines(null);

        Prescription newPrescription = new Prescription();
        newPrescription.setId("PR1");
        newPrescription.setAppointment(appointment1);
        newPrescription.setMedicines(new ArrayList<>());

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(newPrescription);
        when(appointmentSummaryService.getAppointmentSummary("A1")).thenReturn(appointmentSummaryDTO);

        PrescriptionDTO result = prescriptionService.createPrescription(dto);

        assertNotNull(result);
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should update prescription with new appointment and medicines")
    void updatePrescription_shouldUpdatePrescription_whenValidDataProvided() {
        PrescriptionDTO updatedDto = new PrescriptionDTO();
        updatedDto.setAppointment_id("A1");
        updatedDto.setMedicines(List.of(itemDTO1, itemDTO2));

        Appointment newAppointment = new Appointment();
        newAppointment.setId("A2");

        // Create a prescription with mutable medicines list
        Prescription prescriptionWithMutableList = new Prescription();
        prescriptionWithMutableList.setId("PR1");
        prescriptionWithMutableList.setAppointment(appointment1);
        prescriptionWithMutableList.setMedicines(new ArrayList<>());

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescriptionWithMutableList));
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(newAppointment));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescriptionWithMutableList);

        PrescriptionDTO result = prescriptionService.updatePrescription("PR1", updatedDto);

        assertNotNull(result);
        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(appointmentRepository, times(1)).findById("A1");
        verify(prescriptionRepository, times(1)).save(prescriptionWithMutableList);
    }

    @Test
    @DisplayName("Should update prescription without changing appointment when appointment_id is null")
    void updatePrescription_shouldUpdatePrescription_whenAppointmentIdIsNull() {
        PrescriptionDTO updatedDto = new PrescriptionDTO();
        updatedDto.setAppointment_id(null);
        updatedDto.setMedicines(List.of(itemDTO1));

        // Create a prescription with mutable medicines list
        Prescription prescriptionWithMutableList = new Prescription();
        prescriptionWithMutableList.setId("PR1");
        prescriptionWithMutableList.setAppointment(appointment1);
        prescriptionWithMutableList.setMedicines(new ArrayList<>());

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescriptionWithMutableList));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescriptionWithMutableList);

        PrescriptionDTO result = prescriptionService.updatePrescription("PR1", updatedDto);

        assertNotNull(result);
        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(appointmentRepository, never()).findById(anyString());
        verify(prescriptionRepository, times(1)).save(prescriptionWithMutableList);
    }

    @Test
    @DisplayName("Should update prescription with null medicines list")
    void updatePrescription_shouldUpdatePrescription_whenMedicinesIsNull() {
        PrescriptionDTO updatedDto = new PrescriptionDTO();
        updatedDto.setAppointment_id("A1");
        updatedDto.setMedicines(null);

        // Create a prescription with mutable medicines list
        Prescription prescriptionWithMutableList = new Prescription();
        prescriptionWithMutableList.setId("PR1");
        prescriptionWithMutableList.setAppointment(appointment1);
        prescriptionWithMutableList.setMedicines(new ArrayList<>());

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescriptionWithMutableList));
        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescriptionWithMutableList);

        PrescriptionDTO result = prescriptionService.updatePrescription("PR1", updatedDto);

        assertNotNull(result);
        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(prescriptionRepository, times(1)).save(prescriptionWithMutableList);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when prescription not found during update")
    void updatePrescription_shouldThrowException_whenPrescriptionNotFound() {
        PrescriptionDTO updatedDto = prescriptionDTO1;

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.updatePrescription("PR1", updatedDto)
        );

        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found during update")
    void updatePrescription_shouldThrowException_whenAppointmentNotFound() {
        PrescriptionDTO updatedDto = new PrescriptionDTO();
        updatedDto.setAppointment_id("A1");
        updatedDto.setMedicines(List.of(itemDTO1));

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescription1));
        when(appointmentRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.updatePrescription("PR1", updatedDto)
        );

        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(appointmentRepository, times(1)).findById("A1");
        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should delete prescription when prescription exists")
    void deletePrescription_shouldDeletePrescription_whenPrescriptionExists() {
        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescription1));

        prescriptionService.deletePrescription("PR1");

        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(prescriptionRepository, times(1)).delete(prescription1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when prescription not found during delete")
    void deletePrescription_shouldThrowException_whenPrescriptionNotFound() {
        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.deletePrescription("PR1")
        );

        verify(prescriptionRepository, times(1)).findById("PR1");
        verify(prescriptionRepository, never()).delete(any(Prescription.class));
    }

    @Test
    @DisplayName("Should link medicines to prescription during creation")
    void createPrescription_shouldLinkMedicinesToPrescription() {
        PrescriptionDTO dto = prescriptionDTO1;
        Prescription newPrescription = new Prescription();
        newPrescription.setId("PR1");
        newPrescription.setAppointment(appointment1);
        newPrescription.setMedicines(new ArrayList<>());

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment1));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription saved = invocation.getArgument(0);
            if (saved.getMedicines() != null) {
                saved.getMedicines().forEach(item -> {
                    assertNotNull(item.getPrescription());
                    assertEquals(saved, item.getPrescription());
                });
            }
            return saved;
        });
        when(appointmentSummaryService.getAppointmentSummary("A1")).thenReturn(appointmentSummaryDTO);

        prescriptionService.createPrescription(dto);

        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should clear existing medicines and add new ones during update")
    void updatePrescription_shouldClearAndAddNewMedicines() {
        PrescriptionDTO updatedDto = new PrescriptionDTO();
        updatedDto.setAppointment_id(null);
        updatedDto.setMedicines(List.of(itemDTO1));

        // Create a prescription with mutable medicines list and existing items
        Prescription prescriptionWithMutableList = new Prescription();
        prescriptionWithMutableList.setId("PR1");
        prescriptionWithMutableList.setAppointment(appointment1);
        prescriptionWithMutableList.setMedicines(new ArrayList<>());
        prescriptionWithMutableList.getMedicines().add(new PrescriptionItem());

        when(prescriptionRepository.findById("PR1")).thenReturn(Optional.of(prescriptionWithMutableList));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription saved = invocation.getArgument(0);
            assertEquals(1, saved.getMedicines().size());
            return saved;
        });

        prescriptionService.updatePrescription("PR1", updatedDto);

        verify(prescriptionRepository, times(1)).save(prescriptionWithMutableList);
    }
}

