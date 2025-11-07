package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.AppointmentStatus;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.mapper.PrescriptionMapper;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentSummaryService appointmentSummaryService;
    private final PatientVisitHistoryService patientVisitHistoryService;


    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository,
            AppointmentSummaryService appointmentSummaryService,
            PatientVisitHistoryService patientVisitHistoryService) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentSummaryService = appointmentSummaryService;
        this.patientVisitHistoryService = patientVisitHistoryService;
    }

    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        return PrescriptionMapper.toDTO(prescription);
    }

    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Prescription prescription = PrescriptionMapper.toEntity(dto);

        if (dto.getAppointment_id() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointment_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
            prescription.setAppointment(appointment);

            //Update the Appointment Status to Completed
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);

        }
        // Link Each Medicine to prescriptions
        if (prescription.getMedicines() != null) {
            prescription.getMedicines().forEach(item -> item.setPrescription(prescription));
        }

        Prescription saved = prescriptionRepository.save(prescription);

        // Create Appointment Summary for the appointment after prescription
        AppointmentSummaryDTO summaryDTO = (AppointmentSummaryDTO)
                appointmentSummaryService.getAppointmentSummary(saved.getAppointment().getId());

        // Attach the summary to the patient Visit History Table
        patientVisitHistoryService.recordPatientVisitHistory(summaryDTO);

        return PrescriptionMapper.toDTO(saved);
    }

    public PrescriptionDTO updatePrescription(Long id, PrescriptionDTO updatedDto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        if (updatedDto.getAppointment_id() != null) {
            Appointment appointment = appointmentRepository.findById((updatedDto.getAppointment_id()))
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
            existing.setAppointment(appointment);
        }

        // Clear and re-add medicines
        existing.getMedicines().clear();
        if (updatedDto.getMedicines() != null) {
            for (PrescriptionItemDTO itemDTO : updatedDto.getMedicines()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setMedicineName(itemDTO.getMedicineName());
                item.setDosage(itemDTO.getDosage());
                item.setNotes(itemDTO.getNotes());
                item.setPrescription(existing);
                existing.getMedicines().add(item);
            }
        }

        Prescription updated = prescriptionRepository.save(existing);
        return PrescriptionMapper.toDTO(updated);
    }

    public void deletePrescription(Long id) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        prescriptionRepository.delete(existing);
    }
}
