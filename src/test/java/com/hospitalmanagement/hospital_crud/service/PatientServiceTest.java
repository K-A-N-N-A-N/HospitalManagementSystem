package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    PatientRepository patientRepository;

    @Mock
    QueueService queueService;

    @InjectMocks
    PatientService patientService;

    Patient patient1;
    Patient patient2;

    @BeforeEach
    void setUp() {
        patient1 = new Patient();
        patient1.setId("P1");
        patient1.setName("John Doe");
        patient1.setAge(30);
        patient1.setGender("Male");
        patient1.setEmail("john@example.com");
        patient1.setPhoneNumber("1234567890");
        patient1.setAddress("123 Main St");
        patient1.setActive(true);

        patient2 = new Patient();
        patient2.setId("P2");
        patient2.setName("Jane Smith");
        patient2.setAge(25);
        patient2.setGender("Female");
        patient2.setEmail("jane@example.com");
        patient2.setPhoneNumber("9876543210");
        patient2.setAddress("456 Oak Ave");
        patient2.setActive(true);
    }

    @Test
    @DisplayName("Should return list of all ACTIVE patients")
    void getAllActivePatients_shouldReturnActivePatientList() {
        when(patientRepository.findByActiveTrue()).thenReturn(List.of(patient1, patient2));

        List<Patient> result = patientService.getAllActivePatients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("P2", result.get(1).getId());

        verify(patientRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should return list of all INACTIVE patients")
    void getAllInactivePatients_shouldReturnInactivePatientList() {
        patient1.setActive(false);
        patient2.setActive(false);
        when(patientRepository.findByActiveFalse()).thenReturn(List.of(patient1, patient2));

        List<Patient> result = patientService.getAllInactivePatients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());

        verify(patientRepository, times(1)).findByActiveFalse();
    }

    @Test
    @DisplayName("Should return patient when ACTIVE patient with given ID exists")
    void getActivePatientById_shouldReturnPatient_whenPatientExistsAndActive() {
        when(patientRepository.findByIdAndActiveTrue("P1")).thenReturn(Optional.of(patient1));

        Patient result = patientService.getActivePatientById("P1");

        assertNotNull(result);
        assertEquals("P1", result.getId());
        assertEquals("John Doe", result.getName());

        verify(patientRepository, times(1)).findByIdAndActiveTrue("P1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ACTIVE patient not found")
    void getActivePatientById_shouldThrowException_whenPatientNotFound() {
        when(patientRepository.findByIdAndActiveTrue("P1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getActivePatientById("P1")
        );

        verify(patientRepository, times(1)).findByIdAndActiveTrue("P1");
    }

    @Test
    @DisplayName("Should return list of all patients")
    void getAllPatients_shouldReturnAllPatientList() {
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        List<Patient> result = patientService.getAllPatients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("P2", result.get(1).getId());

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return patient when patient with given ID exists")
    void getPatientById_shouldReturnPatient_whenPatientExists() {
        when(patientRepository.findById("P1")).thenReturn(Optional.of(patient1));

        Patient result = patientService.getPatientById("P1");

        assertNotNull(result);
        assertEquals("P1", result.getId());
        assertEquals("John Doe", result.getName());

        verify(patientRepository, times(1)).findById("P1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient not found")
    void getPatientById_shouldThrowException_whenPatientNotFound() {
        when(patientRepository.findById("P1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatientById("P1")
        );

        verify(patientRepository, times(1)).findById("P1");
    }

    @Test
    @DisplayName("Should send patient to ActiveMQ queue and return the patient on create")
    void createPatient_shouldSendToQueueAndReturnPatient() {
        Patient newPatient = patient1;
        Patient result = patientService.createPatient(newPatient);

        assertNotNull(result);
        assertEquals("P1", result.getId());

        verify(queueService, times(1))
                .sendToQueue("patient.queue", newPatient);

        verifyNoInteractions(patientRepository);
    }

    @Test
    @DisplayName("Should update patient fields and send updated patient to queue")
    void updatePatient_shouldUpdateFieldsAndSendToQueue() {
        Patient existing = patient1;

        Patient updatedPatient = new Patient();
        updatedPatient.setName("Updated Name");
        updatedPatient.setEmail("updated@example.com");
        updatedPatient.setPhoneNumber("9999999999");
        updatedPatient.setAddress("789 New St");
        updatedPatient.setAge(35);
        updatedPatient.setGender("Male");

        when(patientRepository.findById("P1"))
                .thenReturn(Optional.of(existing));

        Patient result = patientService.updatePatient("P1", updatedPatient);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("9999999999", result.getPhoneNumber());
        assertEquals("789 New St", result.getAddress());
        assertEquals(35, result.getAge());
        assertEquals("Male", result.getGender());

        verify(patientRepository, times(1)).findById("P1");
        verify(queueService, times(1))
                .sendToQueue("patient.update.queue", existing);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent patient")
    void updatePatient_shouldThrowException_whenPatientNotFound() {
        when(patientRepository.findById("P1"))
                .thenReturn(Optional.empty());

        Patient updatedPatient = patient2;

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.updatePatient("P1", updatedPatient)
        );

        verify(patientRepository, times(1)).findById("P1");
        verifyNoInteractions(queueService);
    }

    @Test
    @DisplayName("Should soft delete active patient and send to delete queue")
    void softDeletePatient_shouldSetInactiveAndSendToQueue() {
        patient1.setActive(true);
        when(patientRepository.findByIdAndActiveTrue("P1"))
                .thenReturn(Optional.of(patient1));

        patientService.softDeletePatient("P1");

        assertFalse(patient1.getActive());

        verify(patientRepository, times(1)).findByIdAndActiveTrue("P1");
        verify(queueService, times(1))
                .sendToQueue("patient.delete.queue", patient1);
    }

    @Test
    @DisplayName("Should throw exception when trying to soft delete non-existent or inactive patient")
    void softDeletePatient_shouldThrowException_whenPatientNotFound() {
        when(patientRepository.findByIdAndActiveTrue("P1"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.softDeletePatient("P1")
        );

        verify(patientRepository, times(1)).findByIdAndActiveTrue("P1");
        verifyNoInteractions(queueService);
    }

}

