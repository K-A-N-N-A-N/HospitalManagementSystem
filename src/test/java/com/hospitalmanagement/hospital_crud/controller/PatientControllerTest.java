package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.service.PatientService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    Patient patient1;
    Patient patient2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        patient1 = new Patient();
        patient1.setId("P1");
        patient1.setName("John Doe");

        patient2 = new Patient();
        patient2.setId("P2");
        patient2.setName("Jane Smith");
    }

    @Test
    @DisplayName("GET /patients/active - Should return active patients")
    void getAllPatients_shouldReturn200() throws Exception {

        when(patientService.getAllActivePatients()).thenReturn(List.of(patient1, patient2));

        mockMvc.perform(get("/patients/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(patientService, times(1)).getAllActivePatients();
    }

    @Test
    @DisplayName("GET /patients/active/{id} - Should return patient by ID")
    void getPatientById_shouldReturn200() throws Exception {

        when(patientService.getActivePatientById("P1")).thenReturn(patient1);

        mockMvc.perform(get("/patients/active/P1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(patientService, times(1)).getActivePatientById("P1");
    }

    @Test
    @DisplayName("GET /patients/inactive - Should return inactive patients")
    void getPatientByIdInactive_shouldReturn200() throws Exception {

        when(patientService.getAllInactivePatients()).thenReturn(List.of(patient1, patient2));

        mockMvc.perform(get("/patients/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(patientService, times(1)).getAllInactivePatients();
    }

    @Test
    @DisplayName("POST /patients - Should create patient")
    void createPatient_shouldReturn200() throws Exception {

        when(patientService.createPatient(any(Patient.class))).thenReturn(patient1);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "John Doe",
                          "email": "john@mail.com",
                          "phoneNumber": "9876543210",
                          "age": 30,
                          "gender": "Male",
                          "address": "123 Main St"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("PUT /patients/{id} - Should update patient")
    void updatePatient_shouldReturn200() throws Exception {

        when(patientService.updatePatient(eq("P1"), any(Patient.class))).thenReturn(patient1);

        mockMvc.perform(put("/patients/P1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Updated Name",
                          "email": "updated@mail.com",
                          "phoneNumber": "9876543210",
                          "age": 31,
                          "gender": "Male",
                          "address": "456 New St"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P1"));

        verify(patientService, times(1)).updatePatient(eq("P1"), any(Patient.class));
    }

    @Test
    @DisplayName("DELETE /patients/delete/{id} - Should soft delete patient")
    void softDeletePatient_shouldReturn200() throws Exception {

        doNothing().when(patientService).softDeletePatient("P1");

        mockMvc.perform(delete("/patients/delete/P1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient status set to Inactive."));

        verify(patientService, times(1)).softDeletePatient("P1");
    }
}


