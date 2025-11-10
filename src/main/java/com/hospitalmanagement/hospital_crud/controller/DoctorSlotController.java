package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.DoctorSlotResponse;
import com.hospitalmanagement.hospital_crud.dto.SlotRequestDTO;
import com.hospitalmanagement.hospital_crud.dto.UpdateSlotDTO;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import com.hospitalmanagement.hospital_crud.service.DoctorSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor-slots")
public class DoctorSlotController {

    private final DoctorSlotService slotService;

    public DoctorSlotController(DoctorSlotService slotService) {
        this.slotService = slotService;
    }

    // Generate default slots for a doctor on a date
    @PostMapping("/generate")
    public ResponseEntity<List<DoctorSlot>> generateSlots(@RequestBody SlotRequestDTO request) {
        List<DoctorSlot> slots = slotService.generateSlots(request.getDoctorId(), request.getDate(), request.getDurationMinutes());
        return ResponseEntity.ok(slots);
    }

    // Get all Scheduled Slots for a specific doctor
    @GetMapping("/scheduledSlots")
    public ResponseEntity<List<DoctorSlotResponse>> scheduledSlots(@RequestParam String doctorId, @RequestParam LocalDate date) {
        List<DoctorSlotResponse> slots = slotService
                .getAllScheduledSlots(doctorId, date)
                .stream()
                .map(DoctorSlotResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slots);
    }

    // Get available slots for a doctor on a specific date
    @GetMapping("/available")
    public ResponseEntity<List<DoctorSlotResponse>> getAvailableSlots(@RequestParam String doctorId, @RequestParam LocalDate date) {
        List<DoctorSlotResponse> slots = slotService
                .getAvailableSlots(doctorId, date)
                .stream()
                .map(DoctorSlotResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(slots);
    }

    //Update Slot Availability
    @PutMapping("/updateSlot")
    public ResponseEntity<String> updateSlot(@RequestBody UpdateSlotDTO request) {
        String message = slotService.updateSlotStatus(
                request.getDoctorId(),
                request.getDate(),
                request.getStartTime(),
                request.isAvailable()
        );
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSlots(@RequestBody SlotRequestDTO request) {
        String result = slotService.deleteSlotsForDate(request.getDoctorId(), request.getDate());

        return ResponseEntity.ok(result);
    }
}
