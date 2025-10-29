package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.mapper.PrescriptionMapper;
import com.hospitalmanagement.hospital_crud.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
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
        Prescription saved = prescriptionRepository.save(prescription);
        return PrescriptionMapper.toDTO(saved);
    }

    public PrescriptionDTO updatePrescription(Long id, PrescriptionDTO updatedDto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

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
