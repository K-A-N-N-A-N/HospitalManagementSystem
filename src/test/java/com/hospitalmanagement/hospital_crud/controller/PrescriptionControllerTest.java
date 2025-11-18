package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.service.PrescriptionService;
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
class PrescriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PrescriptionService prescriptionService;

    @InjectMocks
    private PrescriptionController prescriptionController;

    PrescriptionDTO prescription1;
    PrescriptionDTO prescription2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(prescriptionController).build();

        prescription1 = new PrescriptionDTO();
        prescription1.setId("PR1");
        prescription1.setAppointment_id("A1");

        prescription2 = new PrescriptionDTO();
        prescription2.setId("PR2");
        prescription2.setAppointment_id("A2");
    }

    @Test
    @DisplayName("GET /prescriptions - Should return all prescriptions")
    void getAllPrescriptions_shouldReturn200() throws Exception {

        when(prescriptionService.getAllPrescriptions()).thenReturn(List.of(prescription1, prescription2));

        mockMvc.perform(get("/prescriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("PR1"))
                .andExpect(jsonPath("$[1].id").value("PR2"));

        verify(prescriptionService, times(1)).getAllPrescriptions();
    }

    @Test
    @DisplayName("GET /prescriptions/{id} - Should return prescription by ID")
    void getPrescriptionById_shouldReturn200() throws Exception {

        when(prescriptionService.getPrescriptionById("PR1")).thenReturn(prescription1);

        mockMvc.perform(get("/prescriptions/PR1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PR1"))
                .andExpect(jsonPath("$.appointment_id").value("A1"));

        verify(prescriptionService, times(1)).getPrescriptionById("PR1");
    }

    @Test
    @DisplayName("POST /prescriptions - Should create prescription")
    void addPrescription_shouldReturn200() throws Exception {

        when(prescriptionService.createPrescription(any(PrescriptionDTO.class)))
                .thenReturn(prescription1);

        mockMvc.perform(post("/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "appointment_id": "A1",
                          "medicines": []
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PR1"))
                .andExpect(jsonPath("$.appointment_id").value("A1"));

        verify(prescriptionService, times(1)).createPrescription(any(PrescriptionDTO.class));
    }

    @Test
    @DisplayName("PUT /prescriptions/{id} - Should update prescription")
    void updatePrescription_shouldReturn200() throws Exception {

        when(prescriptionService.updatePrescription(eq("PR1"), any(PrescriptionDTO.class)))
                .thenReturn(prescription1);

        mockMvc.perform(put("/prescriptions/PR1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "appointment_id": "A1",
                          "medicines": []
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PR1"));

        verify(prescriptionService, times(1))
                .updatePrescription(eq("PR1"), any(PrescriptionDTO.class));
    }

    @Test
    @DisplayName("DELETE /prescriptions/{id} - Should delete prescription")
    void deletePrescription_shouldReturn200() throws Exception {

        doNothing().when(prescriptionService).deletePrescription("PR1");

        mockMvc.perform(delete("/prescriptions/PR1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Prescription deleted successfully"));

        verify(prescriptionService, times(1)).deletePrescription("PR1");
    }
}


