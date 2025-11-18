package com.hospitalmanagement.hospital_crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.dto.PatientVisitHistoryDTO;
import com.hospitalmanagement.hospital_crud.entity.PatientVisitHistory;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.PatientVisitHistoryRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientVisitHistoryServiceTest {

    @Mock
    PatientVisitHistoryRepository patientVisitHistoryRepository;

    @InjectMocks
    PatientVisitHistoryService patientVisitHistoryService;

    AppointmentSummaryDTO summaryDTO;

    @BeforeEach
    void setUp() {
        summaryDTO = new AppointmentSummaryDTO(
                "A1",
                "D1",
                "Dr. Strange",
                "P1",
                "John Doe",
                "Flu",
                LocalDateTime.of(2025, 11, 20, 10, 0),
                "COMPLETED",
                "PR1",
                List.of(new PrescriptionItemDTO("PI1", "Med", "10mg", "Twice"))
        );
    }

    @Test
    @DisplayName("Should record patient visit history successfully")
    void recordPatientVisitHistory_shouldPersistEntity() {
        ArgumentCaptor<PatientVisitHistory> captor = ArgumentCaptor.forClass(PatientVisitHistory.class);

        patientVisitHistoryService.recordPatientVisitHistory(summaryDTO);

        verify(patientVisitHistoryRepository, times(1)).save(captor.capture());

        PatientVisitHistory saved = captor.getValue();
        assertEquals("P1", saved.getPatientId());
        assertEquals("John Doe", saved.getPatientName());
        assertEquals("A1", saved.getAppointmentId());
        assertEquals(summaryDTO.getAppointmentDate(), saved.getVisitDate());
        assertNotNull(saved.getAppointmentSummaryJson());
        assertTrue(saved.getAppointmentSummaryJson().contains("\"appointmentId\":\"A1\""));
    }

    @Test
    @DisplayName("Should throw RuntimeException when serialization fails while recording history")
    void recordPatientVisitHistory_shouldThrowRuntimeException_whenSerializationFails() throws Exception {
        PatientVisitHistoryService failingService =
                new PatientVisitHistoryService(patientVisitHistoryRepository);

        Field field = PatientVisitHistoryService.class.getDeclaredField("objectMapper");
        field.setAccessible(true);
        ObjectMapper failingMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("forced failure") {};
            }
        };
        field.set(failingService, failingMapper);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> failingService.recordPatientVisitHistory(summaryDTO));

        assertTrue(exception.getMessage().contains("Error serializing appointment summary"));
        verifyNoInteractions(patientVisitHistoryRepository);
    }

    @Test
    @DisplayName("Should return visit history DTOs for patient")
    void getVisitHistoryByPatient_shouldReturnVisitHistory() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(summaryDTO);

        PatientVisitHistory history = PatientVisitHistory.builder()
                .id("VH1")
                .patientId("P1")
                .patientName("John Doe")
                .appointmentId("A1")
                .visitDate(summaryDTO.getAppointmentDate())
                .appointmentSummaryJson(json)
                .build();

        when(patientVisitHistoryRepository.findByPatientId("P1"))
                .thenReturn(List.of(history));

        List<PatientVisitHistoryDTO> result = patientVisitHistoryService.getVisitHistoryByPatient("P1");

        assertEquals(1, result.size());
        PatientVisitHistoryDTO dto = result.get(0);
        assertEquals("VH1", dto.getId());
        assertEquals("P1", dto.getPatientId());
        assertEquals("Dr. Strange", dto.getAppointmentSummary().getDoctorName());

        verify(patientVisitHistoryRepository, times(1)).findByPatientId("P1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no visit history exists for patient")
    void getVisitHistoryByPatient_shouldThrowException_whenNoHistory() {
        when(patientVisitHistoryRepository.findByPatientId("P2"))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> patientVisitHistoryService.getVisitHistoryByPatient("P2"));

        verify(patientVisitHistoryRepository, times(1)).findByPatientId("P2");
    }
}

