package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.DoctorSlotResponse;
import com.hospitalmanagement.hospital_crud.dto.SlotRequestDTO;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import com.hospitalmanagement.hospital_crud.service.DoctorSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-slots")
public class DoctorSlotController {

    private final DoctorSlotService slotService;

    public DoctorSlotController(DoctorSlotService slotService) {
        this.slotService = slotService;
    }

    // Generate default slots for a doctor on a date
    @PostMapping("/generate")
    public ResponseEntity<List<DoctorSlot>> generateDefaultSlots(@RequestBody SlotRequestDTO request) {
        List<DoctorSlot> slots = slotService.generateDefaultSlots(request.getDoctorId(), request.getDate());
        return ResponseEntity.ok(slots);
    }

    // Get available slots for a doctor on a specific date
    @PostMapping("/available")
    public ResponseEntity<List<DoctorSlotResponse>> getAvailableSlots(@RequestBody SlotRequestDTO request) {
        List<DoctorSlotResponse> slots = slotService
                .getAvailableSlots(request.getDoctorId(), request.getDate())
                .stream()
                .map(DoctorSlotResponse::new)
                .toList();

        return ResponseEntity.ok(slots);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSlots(@RequestBody SlotRequestDTO request) {
        String result = slotService.deleteSlotsForDate(request.getDoctorId(), request.getDate());

        return ResponseEntity.ok(result);
    }
}
