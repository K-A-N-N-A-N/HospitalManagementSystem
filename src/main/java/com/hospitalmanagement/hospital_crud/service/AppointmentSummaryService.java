package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.PrescriptionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentSummaryService {

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentSummaryService(AppointmentRepository appointmentRepository,
                                     PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public Object getAppointmentSummary(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // If not completed, return a friendly message instead of throwing an exception
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return new ResponseMessage("Appointment is not completed yet. Prescription will be available after completion.");
        }

        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        Optional<Prescription> optionalPrescription = prescriptionRepository.findByAppointmentId(appointmentId);
        if (optionalPrescription.isEmpty()) {
            return new ResponseMessage("No prescription found for this completed appointment.");
        }

        Prescription prescription = optionalPrescription.get();

        List<PrescriptionItemDTO> medicines = prescription.getMedicines().stream()
                .map(item -> new PrescriptionItemDTO(
                        item.getId(),
                        item.getMedicineName(),
                        item.getDosage(),
                        item.getNotes(),
                        item.getSku(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new AppointmentSummaryDTO(
                appointment.getId(),
                doctor.getId(),
                doctor.getName(),
                patient.getId(),
                patient.getName(),
                appointment.getReason(),
                appointment.getAppointmentTime(),
                appointment.getStatus().name(),
                prescription.getId(),
                medicines
        );
    }

    @Data
    @AllArgsConstructor
    static class ResponseMessage {
        private String message;
    }
}
