package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.service.DoctorService;
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
class DoctorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    Doctor doctor1;
    Doctor doctor2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        doctor1 = new Doctor();
        doctor1.setId("D1");
        doctor1.setName("John Doe");

        doctor2 = new Doctor();
        doctor2.setId("D2");
        doctor2.setName("Jane Smith");
    }

    @Test
    @DisplayName("GET /doctors/active - Should return active doctors")
    void getAllActiveDoctors_shouldReturn200() throws Exception {

        when(doctorService.getAllActiveDoctors()).thenReturn(List.of(doctor1, doctor2));

        mockMvc.perform(get("/doctors/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(doctorService, times(1)).getAllActiveDoctors();
    }

    @Test
    @DisplayName("GET /doctors/active/{id} - Should return doctor by ID")
    void getDoctorById_shouldReturn200() throws Exception {

        when(doctorService.getActiveDoctorById("D1")).thenReturn(doctor1);

        mockMvc.perform(get("/doctors/active/D1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("D1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(doctorService, times(1)).getActiveDoctorById("D1");
    }

    @Test
    @DisplayName("GET /doctors/inactive - Should return inactive doctors")
    void getAllInactiveDoctors_shouldReturn200() throws Exception {

        when(doctorService.getAllInactiveDoctors()).thenReturn(List.of(doctor1, doctor2));

        mockMvc.perform(get("/doctors/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(doctorService, times(1)).getAllInactiveDoctors();
    }

    @Test
    @DisplayName("POST /doctors - Should create doctor")
    void createDoctor_shouldReturn200() throws Exception {

        when(doctorService.createDoctor(any(Doctor.class))).thenReturn(doctor1);

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "John Doe",
                          "email": "john@mail.com",
                          "phoneNumber": "9876543210"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("D1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(doctorService, times(1)).createDoctor(any(Doctor.class));
    }

    @Test
    @DisplayName("PUT /doctors/{id} - Should update doctor")
    void updateDoctor_shouldReturn200() throws Exception {

        when(doctorService.updateDoctor(eq("D1"), any(Doctor.class))).thenReturn(doctor1);

        mockMvc.perform(put("/doctors/D1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Updated Name",
                          "email": "updated@mail.com",
                          "phoneNumber": "9876543210"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("D1"));

        verify(doctorService, times(1)).updateDoctor(eq("D1"), any(Doctor.class));
    }

    @Test
    @DisplayName("DELETE /doctors/delete/{id} - Should soft delete doctor")
    void softDeleteDoctor_shouldReturn200() throws Exception {

        doNothing().when(doctorService).softDeleteDoctor("D1");

        mockMvc.perform(delete("/doctors/delete/D1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor status set to Inactive."));

        verify(doctorService, times(1)).softDeleteDoctor("D1");
    }
}
