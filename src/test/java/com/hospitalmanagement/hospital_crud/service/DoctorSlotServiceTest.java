package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorSlotServiceTest {

    @Mock
    DoctorSlotRepository slotRepository;

    @Mock
    DoctorRepository doctorRepository;

    @InjectMocks
    DoctorSlotService doctorSlotService;

    Doctor doctor;
    LocalDate date;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId("D1");
        doctor.setName("Dr. House");

        date = LocalDate.of(2025, 11, 20);
    }

    @Test
    @DisplayName("Should generate slots for doctor with valid duration")
    void generateSlots_shouldCreateSlots_whenDurationValid() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<DoctorSlot> result = doctorSlotService.generateSlots("D1", date, 60);

        assertEquals(8, result.size()); // 8 hours (9-17) with 60-minute slots
        verify(doctorRepository, times(1)).findById("D1");
        verify(slotRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw RuntimeException when doctor not found for slot generation")
    void generateSlots_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> doctorSlotService.generateSlots("D1", date, 60));

        verify(slotRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when slot duration invalid")
    void generateSlots_shouldThrowException_whenDurationInvalid() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));

        assertThrows(IllegalArgumentException.class,
                () -> doctorSlotService.generateSlots("D1", date, 0));

        assertThrows(IllegalArgumentException.class,
                () -> doctorSlotService.generateSlots("D1", date, 180));
    }

    @Test
    @DisplayName("Should return scheduled slots (non-available)")
    void getAllScheduledSlots_shouldReturnNonAvailableSlots() {
        DoctorSlot slotAvailable = DoctorSlot.builder().doctor(doctor).available(true).build();
        DoctorSlot slotBooked = DoctorSlot.builder().doctor(doctor).available(false).build();

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDate("D1", date))
                .thenReturn(List.of(slotAvailable, slotBooked));

        List<DoctorSlot> result = doctorSlotService.getAllScheduledSlots("D1", date);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isAvailable());
    }

    @Test
    @DisplayName("Should throw RuntimeException when doctor not found for scheduled slots")
    void getAllScheduledSlots_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> doctorSlotService.getAllScheduledSlots("D1", date));
    }

    @Test
    @DisplayName("Should return available slots only")
    void getAvailableSlots_shouldReturnAvailableSlots() {
        DoctorSlot slotAvailable = DoctorSlot.builder().doctor(doctor).available(true).build();
        DoctorSlot slotBooked = DoctorSlot.builder().doctor(doctor).available(false).build();

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDate("D1", date))
                .thenReturn(List.of(slotAvailable, slotBooked));

        List<DoctorSlot> result = doctorSlotService.getAvailableSlots("D1", date);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    @DisplayName("Should throw RuntimeException when doctor not found for available slots")
    void getAvailableSlots_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> doctorSlotService.getAvailableSlots("D1", date));
    }

    @Test
    @DisplayName("Should update slot status and return message")
    void updateSlotStatus_shouldUpdateSlot() {
        DoctorSlot slot = DoctorSlot.builder()
                .doctor(doctor)
                .date(date)
                .startTime(LocalTime.of(9, 0))
                .available(false)
                .build();

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDateAndStartTime("D1", date, LocalTime.of(9, 0)))
                .thenReturn(Optional.of(slot));
        when(slotRepository.save(slot)).thenReturn(slot);

        String result = doctorSlotService.updateSlotStatus("D1", date, LocalTime.of(9, 0), true);

        assertTrue(slot.isAvailable());
        assertTrue(result.contains("updated to available = true"));
        verify(slotRepository, times(1)).save(slot);
    }

    @Test
    @DisplayName("Should throw RuntimeException when doctor not found for slot update")
    void updateSlotStatus_shouldThrowException_whenDoctorNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> doctorSlotService.updateSlotStatus("D1", date, LocalTime.of(9, 0), true));
    }

    @Test
    @DisplayName("Should throw RuntimeException when slot not found for update")
    void updateSlotStatus_shouldThrowException_whenSlotNotFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDateAndStartTime("D1", date, LocalTime.of(9, 0)))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> doctorSlotService.updateSlotStatus("D1", date, LocalTime.of(9, 0), true));
    }

    @Test
    @DisplayName("Should delete slots for date and return message")
    void deleteSlotsForDate_shouldDeleteSlots() {
        DoctorSlot slot = DoctorSlot.builder().doctor(doctor).build();

        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDate("D1", date))
                .thenReturn(List.of(slot));

        String result = doctorSlotService.deleteSlotsForDate("D1", date);

        assertTrue(result.contains("Deleted 1 slots"));
        verify(slotRepository, times(1)).deleteAll(List.of(slot));
    }

    @Test
    @DisplayName("Should return message when no slots found to delete")
    void deleteSlotsForDate_shouldReturnMessage_whenNoSlotsFound() {
        when(doctorRepository.findById("D1")).thenReturn(Optional.of(doctor));
        when(slotRepository.findByDoctorIdAndDate("D1", date))
                .thenReturn(List.of());

        String result = doctorSlotService.deleteSlotsForDate("D1", date);

        assertTrue(result.contains("No slots found"));
        verify(slotRepository, never()).deleteAll(anyList());
    }
}

