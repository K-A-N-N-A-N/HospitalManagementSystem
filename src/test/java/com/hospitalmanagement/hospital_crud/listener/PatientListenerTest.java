package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientListenerTest {

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    PatientListener patientListener;

    Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId("P1");
        patient.setName("Kannan");
        patient.setActive(true);
    }

    @Test
    @DisplayName("Should save patient when CREATE message is received")
    void handleCreate_shouldSavePatient() {
        patientListener.handleCreate(patient);

        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    @DisplayName("Should save patient when UPDATE message is received")
    void handleUpdate_shouldSavePatient() {
        patientListener.handleUpdate(patient);

        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    @DisplayName("Should soft delete patient when DELETE message is received")
    void handleDelete_shouldSoftDeletePatient() {
        patientListener.handleDelete(patient);

        assertFalse(patient.getActive()); // soft delete flag
        verify(patientRepository, times(1)).save(patient);
    }
}
