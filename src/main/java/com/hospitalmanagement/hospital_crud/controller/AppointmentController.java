package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.dto.AppointmentRequest;
import com.hospitalmanagement.hospital_crud.dto.AppointmentResponse;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.service.AppointmentService;
import com.hospitalmanagement.hospital_crud.service.AppointmentSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentSummaryService appointmentSummaryService;

    public AppointmentController(AppointmentService appointmentService, AppointmentSummaryService appointmentSummaryService) {
        this.appointmentService = appointmentService;
        this.appointmentSummaryService = appointmentSummaryService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.createAppointment(request);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public AppointmentResponse updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.updateAppointment(id, request);
        return new AppointmentResponse(appointment);
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments()
                .stream()
                .map(AppointmentResponse::new)
                .toList();
    }

    @GetMapping("/active")
    public List<AppointmentResponse> getAllActiveAppointments() {
        return appointmentService.getAllActiveAppointments()
                .stream()
                .map(AppointmentResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public AppointmentResponse getAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return new AppointmentResponse(appointment);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        String msg = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("/{id}")
    public String deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return "Appointment deleted successfully";
    }

    //Display Appointment Summary
    @GetMapping("/summary/{id}")
    public ResponseEntity<?> getAppointmentSummary(@PathVariable Long id) {
        Object result = appointmentSummaryService.getAppointmentSummary(id);
        return ResponseEntity.ok(result);
    }

}
