package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorListenerTest {

    @Mock
    DoctorRepository doctorRepository;

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
}
