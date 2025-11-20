package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.PatientVisitHistoryDTO;
import com.hospitalmanagement.hospital_crud.service.PatientVisitHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PatientVisitHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientVisitHistoryService patientVisitHistoryService;

    @InjectMocks
    private PatientVisitHistoryController patientVisitHistoryController;

    PatientVisitHistoryDTO history1;
    PatientVisitHistoryDTO history2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientVisitHistoryController).build();

        history1 = PatientVisitHistoryDTO.builder()
                .id("VH1")
                .patientId("P1")
                .patientName("John Doe")
                .visitDate(LocalDateTime.of(2025, 11, 20, 10, 0))
                .appointmentSummary(null)
                .build();

        history2 = PatientVisitHistoryDTO.builder()
                .id("VH2")
                .patientId("P1")
                .patientName("John Doe")
                .visitDate(LocalDateTime.of(2025, 11, 21, 11, 0))
                .appointmentSummary(null)
                .build();
    }

    @Test
    @DisplayName("GET /patientVisitHistory/{id} - Should return visit history list")
    void getPatientVisitHistory_shouldReturn200() throws Exception {
        when(patientVisitHistoryService.getVisitHistoryByPatient("P1"))
                .thenReturn(List.of(history1, history2));

        mockMvc.perform(get("/patientVisitHistory/P1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("VH1"))
                .andExpect(jsonPath("$[1].id").value("VH2"));

        verify(patientVisitHistoryService, times(1))
                .getVisitHistoryByPatient("P1");
    }
}


