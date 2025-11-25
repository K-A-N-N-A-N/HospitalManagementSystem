package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientListenerTest {

    @Mock
    PatientRepository patientRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

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
    @DisplayName("Should create User when patient has username and rawPassword")
    void handleCreate_shouldCreateUser_whenCredentialsPresent() {

        patient.setUsername("kannan");
        patient.setRawPassword("pass123");

        when(bCryptPasswordEncoder.encode("pass123")).thenReturn("encoded-pass");

        PatientListener listener = new PatientListener(patientRepository, userRepository, bCryptPasswordEncoder);
        listener.handleCreate(patient);

        verify(patientRepository, times(1)).save(patient);
        verify(bCryptPasswordEncoder, times(1)).encode("pass123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("handleCreate - Should NOT create User when username or rawPassword is missing")
    void handleCreate_shouldNotCreateUser_whenCredentialsMissing() {

        PatientListener listener = new PatientListener(patientRepository, userRepository, bCryptPasswordEncoder);

        listener.handleCreate(patient);

        verify(patientRepository, times(1)).save(patient);
        verify(userRepository, times(0)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(0)).encode(anyString());
    }

    @Test
    @DisplayName("handleCreate - Should NOT create User when rawPassword is missing but username is present")
    void handleCreate_shouldNotCreateUser_whenOnlyUsernamePresent() {

        patient.setUsername("kannan");
        patient.setRawPassword(null); // rawPassword missing

        PatientListener listener = new PatientListener(patientRepository, userRepository, bCryptPasswordEncoder);

        listener.handleCreate(patient);

        verify(patientRepository, times(1)).save(patient);
        verify(userRepository, never()).save(any(User.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
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

        assertFalse(patient.getActive());
        verify(patientRepository, times(1)).save(patient);
    }
}
