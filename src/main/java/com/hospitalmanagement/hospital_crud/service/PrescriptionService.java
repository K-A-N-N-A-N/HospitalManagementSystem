package com.hospitalmanagement.hospital_crud.service;

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

    public PrescriptionService(PrescriptionRepository prescriptionRepository, AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
        return PrescriptionMapper.toDTO(prescription);
    }

    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Prescription prescription = PrescriptionMapper.toEntity(dto);

        if (dto.getAppointment_id() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointment_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + dto.getAppointment_id()));
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
        return PrescriptionMapper.toDTO(saved);
    }

    public PrescriptionDTO updatePrescription(Long id, PrescriptionDTO updatedDto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        if (updatedDto.getAppointment_id() != null) {
            Appointment appointment = appointmentRepository.findById((updatedDto.getAppointment_id()))
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + updatedDto.getAppointment_id()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
        prescriptionRepository.delete(existing);
    }
}
