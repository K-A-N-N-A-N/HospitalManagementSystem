package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    QueueService queueService;

    @Mock
    JmsTemplate jmsTemplate;

    @InjectMocks
    DoctorService doctorService;

    Doctor doctor1;
    Doctor doctor2;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor();
        doctor1.setId("D1");
        doctor1.setName("John Doe");

        doctor2 = new Doctor();
        doctor2.setId("D2");
        doctor2.setName("Jane Smith");
    }

    @Test
    @DisplayName("Should return list of all ACTIVE doctors")
    void getAllActiveDoctors_shouldReturnActiveDoctorList() {
        when(doctorRepository.findByActiveTrue()).thenReturn(List.of(doctor1, doctor2));

        List<Doctor> result = doctorService.getAllActiveDoctors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("D2", result.get(1).getId());

        verify(doctorRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should return list of all INACTIVE doctors")
    void getAllInactiveDoctors_shouldReturnInactiveDoctorList() {
        when(doctorRepository.findByActiveFalse()).thenReturn(List.of(doctor1, doctor2));

        List<Doctor> result = doctorService.getAllInactiveDoctors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());

        verify(doctorRepository, times(1)).findByActiveFalse();
    }

    @Test
    @DisplayName("Should return doctor when ACTIVE doctor with given ID exists")
    void getActiveDoctorById_shouldReturnDoctor_whenDoctorExistsAndActive() {
        when(doctorRepository.findByIdAndActiveTrue("D1")).thenReturn(Optional.of(doctor1));

        Doctor result = doctorService.getActiveDoctorById("D1");

        assertNotNull(result);
        assertEquals("D1", result.getId());
        assertEquals("John Doe", result.getName());

        verify(doctorRepository, times(1)).findByIdAndActiveTrue("D1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ACTIVE doctor not found")
    void getActiveDoctorById_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findByIdAndActiveTrue("D1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.getActiveDoctorById("D1")
        );

        verify(doctorRepository, times(1)).findByIdAndActiveTrue("D1");
    }

    @Test
    @DisplayName("Should send doctor to ActiveMQ queue and return the doctor on create")
    void createDoctor_shouldSendToQueueAndReturnDoctor() {
        Doctor newDoctor = doctor1;
        Doctor result = doctorService.createDoctor(newDoctor);

        assertNotNull(result);
        assertEquals("D1", result.getId());

        verify(queueService, times(1))
                .sendToQueue("doctor.queue", newDoctor);

        verifyNoInteractions(doctorRepository);
    }

    @Test
    @DisplayName("Should update doctor fields and send updated doctor to queue")
    void updateDoctor_shouldUpdateFieldsAndSendToQueue() {
        Doctor existing = doctor1;

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setName("Updated Name");
        updatedDoctor.setEmail("updated@mail.com");
        updatedDoctor.setPhoneNumber("9999999999");
        updatedDoctor.setSpecialization("Orthopedic");

        when(doctorRepository.findById("D1"))
                .thenReturn(Optional.of(existing));

        Doctor result = doctorService.updateDoctor("D1", updatedDoctor);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated@mail.com", result.getEmail());
        assertEquals("9999999999", result.getPhoneNumber());
        assertEquals("Orthopedic", result.getSpecialization());

        verify(doctorRepository, times(1)).findById("D1");
        verify(queueService, times(1))
                .sendToQueue("doctor.update.queue", existing);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent doctor")
    void updateDoctor_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1"))
                .thenReturn(Optional.empty());

        Doctor updatedDoctor = doctor2;

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.updateDoctor("D1", updatedDoctor)
        );

        verify(doctorRepository, times(1)).findById("D1");
        verifyNoInteractions(queueService);
    }

    @Test
    @DisplayName("Should soft delete active doctor and send to delete queue")
    void softDeleteDoctor_shouldSetInactiveAndSendToQueue() {
        doctor1.setActive(true);
        when(doctorRepository.findByIdAndActiveTrue("D1"))
                .thenReturn(Optional.of(doctor1));

        doctorService.softDeleteDoctor("D1");

        assertFalse(doctor1.getActive());

        verify(doctorRepository, times(1)).findByIdAndActiveTrue("D1");
        verify(queueService, times(1))
                .sendToQueue("doctor.delete.queue", doctor1);
    }

    @Test
    @DisplayName("Should throw exception when trying to soft delete non-existent or inactive doctor")
    void softDeleteDoctor_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findByIdAndActiveTrue("D1"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.softDeleteDoctor("D1")
        );

        verify(doctorRepository, times(1)).findByIdAndActiveTrue("D1");
        verifyNoInteractions(queueService);
    }

}
