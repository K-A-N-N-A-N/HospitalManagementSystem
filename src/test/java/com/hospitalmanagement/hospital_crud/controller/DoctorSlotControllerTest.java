package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import com.hospitalmanagement.hospital_crud.service.DoctorSlotService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorSlotControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorSlotService doctorSlotService;

    @InjectMocks
    private DoctorSlotController doctorSlotController;

    Doctor doctor;
    DoctorSlot slot1;
    DoctorSlot slot2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorSlotController).build();

        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. House");

        slot1 = DoctorSlot.builder()
                .id("S1")
                .doctor(doctor)
                .date(LocalDate.of(2025, 11, 20))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .available(true)
                .build();

        slot2 = DoctorSlot.builder()
                .id("S2")
                .doctor(doctor)
                .date(LocalDate.of(2025, 11, 20))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .available(false)
                .build();
    }

    @Test
    @DisplayName("POST /api/doctor-slots/generate - Should generate slots")
    void generateSlots_shouldReturn200() throws Exception {

        when(doctorSlotService.generateSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20)), eq(60)))
                .thenReturn(List.of(slot1, slot2));

        mockMvc.perform(post("/api/doctor-slots/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "date": "2025-11-20",
                          "durationMinutes": 60
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("S1"))
                .andExpect(jsonPath("$[1].id").value("S2"));

        verify(doctorSlotService, times(1))
                .generateSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20)), eq(60));
    }

    @Test
    @DisplayName("GET /api/doctor-slots/scheduledSlots - Should return scheduled slots")
    void scheduledSlots_shouldReturn200() throws Exception {

        when(doctorSlotService.getAllScheduledSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20))))
                .thenReturn(List.of(slot1, slot2));

        mockMvc.perform(get("/api/doctor-slots/scheduledSlots")
                        .param("doctorId", "D1")
                        .param("date", "2025-11-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].doctorId").value("D1"));

        verify(doctorSlotService, times(1))
                .getAllScheduledSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20)));
    }

    @Test
    @DisplayName("GET /api/doctor-slots/available - Should return available slots")
    void getAvailableSlots_shouldReturn200() throws Exception {

        when(doctorSlotService.getAvailableSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20))))
                .thenReturn(List.of(slot1));

        mockMvc.perform(get("/api/doctor-slots/available")
                        .param("doctorId", "D1")
                        .param("date", "2025-11-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("S1"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(doctorSlotService, times(1))
                .getAvailableSlots(eq("D1"), eq(LocalDate.of(2025, 11, 20)));
    }

    @Test
    @DisplayName("PUT /api/doctor-slots/updateSlot - Should update slot availability")
    void updateSlot_shouldReturn200() throws Exception {

        when(doctorSlotService.updateSlotStatus(eq("D1"),
                eq(LocalDate.of(2025, 11, 20)),
                eq(LocalTime.of(9, 0)),
                eq(false)))
                .thenReturn("Slot updated");

        mockMvc.perform(put("/api/doctor-slots/updateSlot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "date": "2025-11-20",
                          "startTime": "09:00:00",
                          "available": false
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Slot updated"));

        verify(doctorSlotService, times(1))
                .updateSlotStatus(eq("D1"),
                        eq(LocalDate.of(2025, 11, 20)),
                        eq(LocalTime.of(9, 0)),
                        eq(false));
    }

    @Test
    @DisplayName("DELETE /api/doctor-slots/delete - Should delete slots for date")
    void deleteSlots_shouldReturn200() throws Exception {

        when(doctorSlotService.deleteSlotsForDate(eq("D1"), eq(LocalDate.of(2025, 11, 20))))
                .thenReturn("Deleted 2 slots");

        mockMvc.perform(delete("/api/doctor-slots/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "doctorId": "D1",
                          "date": "2025-11-20",
                          "durationMinutes": 60
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted 2 slots"));

        verify(doctorSlotService, times(1))
                .deleteSlotsForDate(eq("D1"), eq(LocalDate.of(2025, 11, 20)));
    }
}


