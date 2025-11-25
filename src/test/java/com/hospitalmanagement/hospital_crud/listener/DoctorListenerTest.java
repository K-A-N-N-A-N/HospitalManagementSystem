package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.User;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorListenerTest {

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    DoctorListener doctorListener;

    Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. John");
    }

    @Test
    @DisplayName("Should save doctor when CREATE message received")
    void handleCreate_shouldSaveDoctor() {
        doctorListener.handleCreate(doctor);

        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    @DisplayName("Should save doctor when UPDATE message received")
    void handleUpdate_shouldSaveDoctor() {
        doctorListener.handleUpdate(doctor);

        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    @DisplayName("Should save doctor when DELETE message received")
    void handleDelete_shouldSaveDoctor() {
        doctorListener.handleDelete(doctor);

        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    @DisplayName("handleCreate - Should create User when doctor has username and rawPassword")
    void handleCreate_shouldCreateUser_whenCredentialsPresent() {

        doctor.setUsername("doctor1");
        doctor.setRawPassword("docpass123");

        when(bCryptPasswordEncoder.encode("docpass123")).thenReturn("encoded-pass");

        DoctorListener listener =
                new DoctorListener(doctorRepository, userRepository, bCryptPasswordEncoder);

        listener.handleCreate(doctor);

        verify(doctorRepository, times(1)).save(doctor);
        verify(bCryptPasswordEncoder, times(1)).encode("docpass123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("handleCreate - Should NOT create User when only username is present")
    void handleCreate_shouldNotCreateUser_whenOnlyUsernamePresent() {

        doctor.setUsername("doctor1");
        doctor.setRawPassword(null); // password missing

        DoctorListener listener =
                new DoctorListener(doctorRepository, userRepository, bCryptPasswordEncoder);

        listener.handleCreate(doctor);

        verify(doctorRepository, times(1)).save(doctor);
        verify(userRepository, never()).save(any(User.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("handleCreate - Should NOT create User when username is missing")
    void handleCreate_shouldNotCreateUser_whenUsernameMissing() {

        doctor.setUsername(null);
        doctor.setRawPassword("pass123");

        DoctorListener listener =
                new DoctorListener(doctorRepository, userRepository, bCryptPasswordEncoder);

        listener.handleCreate(doctor);

        verify(doctorRepository, times(1)).save(doctor);
        verify(userRepository, never()).save(any(User.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }
}
