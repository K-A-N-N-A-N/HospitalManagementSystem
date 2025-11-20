package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.AppointmentRequest;
import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.AppointmentStatus;
import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.Patient;
import com.hospitalmanagement.hospital_crud.service.AppointmentService;
import com.hospitalmanagement.hospital_crud.service.AppointmentSummaryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentSummaryService appointmentSummaryService;

    @InjectMocks
    private AppointmentController appointmentController;

    Appointment appointment1;
    Appointment appointment2;
    Doctor doctor;
    Patient patient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. Smith");

        patient = new Patient();
        patient.setId("P1");
        patient.setName("John Doe");

        appointment1 = new Appointment();
        appointment1.setId("A1");
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setAppointmentTime(LocalDateTime.of(2025, 11, 20, 10, 0));
        appointment1.setReason("Flu");
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        appointment2 = new Appointment();
        appointment2.setId("A2");
        appointment2.setDoctor(doctor);
        appointment2.setPatient(patient);
        appointment2.setAppointmentTime(LocalDateTime.of(2025, 11, 21, 11, 0));
        appointment2.setReason("Follow-up");
        appointment2.setStatus(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("POST /appointments/book - Should book appointment successfully")
    void bookAppointment_shouldReturn200_whenSuccess() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentRequest.class)))
                .thenReturn(appointment1);

        mockMvc.perform(post("/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "patientId": "P1",
                          "appointmentTime": "2025-11-20T10:00:00",
                          "reason": "Flu"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.reason").value("Flu"));

        verify(appointmentService, times(1)).createAppointment(any(AppointmentRequest.class));
    }

    @Test
    @DisplayName("POST /appointments/book - Should return 400 when booking fails")
    void bookAppointment_shouldReturn400_whenServiceThrows() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentRequest.class)))
                .thenThrow(new RuntimeException("Slot not available"));

        mockMvc.perform(post("/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "patientId": "P1",
                          "appointmentTime": "2025-11-20T10:00:00",
                          "reason": "Flu"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Slot not available"));

        verify(appointmentService, times(1)).createAppointment(any(AppointmentRequest.class));
    }

    @Test
    @DisplayName("PUT /appointments/{id} - Should update appointment")
    void updateAppointment_shouldReturn200() throws Exception {
        when(appointmentService.updateAppointment(eq("A1"), any(AppointmentRequest.class)))
                .thenReturn(appointment1);

        mockMvc.perform(put("/appointments/A1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "patientId": "P1",
                          "appointmentTime": "2025-11-21T11:00:00",
                          "reason": "Updated Reason"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A1"));

        verify(appointmentService, times(1))
                .updateAppointment(eq("A1"), any(AppointmentRequest.class));
    }

    @Test
    @DisplayName("GET /appointments - Should return all appointments")
    void getAllAppointments_shouldReturn200() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointment1, appointment2));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("A1"))
                .andExpect(jsonPath("$[1].id").value("A2"));

        verify(appointmentService, times(1)).getAllAppointments();
    }

    @Test
    @DisplayName("GET /appointments/active - Should return active appointments")
    void getAllActiveAppointments_shouldReturn200() throws Exception {
        when(appointmentService.getAllActiveAppointments()).thenReturn(List.of(appointment1));

        mockMvc.perform(get("/appointments/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("A1"));

        verify(appointmentService, times(1)).getAllActiveAppointments();
    }

    @Test
    @DisplayName("GET /appointments/{id} - Should return appointment by ID")
    void getAppointment_shouldReturn200() throws Exception {
        when(appointmentService.getAppointmentById("A1")).thenReturn(appointment1);

        mockMvc.perform(get("/appointments/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.doctorId").value("D1"))
                .andExpect(jsonPath("$.patientId").value("P1"));

        verify(appointmentService, times(1)).getAppointmentById("A1");
    }

    @Test
    @DisplayName("PUT /appointments/cancel/{id} - Should cancel appointment")
    void cancelAppointment_shouldReturn200() throws Exception {
        when(appointmentService.cancelAppointment("A1"))
                .thenReturn("Appointment has been successfully Cancelled");

        mockMvc.perform(put("/appointments/cancel/A1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment has been successfully Cancelled"));

        verify(appointmentService, times(1)).cancelAppointment("A1");
    }

    @Test
    @DisplayName("DELETE /appointments/{id} - Should delete appointment")
    void deleteAppointmentById_shouldReturn200() throws Exception {
        doNothing().when(appointmentService).deleteAppointment("A1");

        mockMvc.perform(delete("/appointments/A1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment deleted successfully"));

        verify(appointmentService, times(1)).deleteAppointment("A1");
    }

    @Test
    @DisplayName("GET /appointments/summary/{id} - Should return appointment summary")
    void getAppointmentSummary_shouldReturn200() throws Exception {
        AppointmentSummaryDTO summaryDTO = new AppointmentSummaryDTO();
        summaryDTO.setAppointmentId("A1");

        when(appointmentSummaryService.getAppointmentSummary("A1"))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/appointments/summary/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value("A1"));

        verify(appointmentSummaryService, times(1))
                .getAppointmentSummary("A1");
    }
}


