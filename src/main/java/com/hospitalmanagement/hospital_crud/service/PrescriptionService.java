package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    private PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<Prescription> getAllPrescription(){
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
    }

    public Prescription createPrescription(Prescription prescription) {
        if (prescription.getMedicines() != null){
            for (PrescriptionItem pi : prescription.getMedicines()) {
                pi.setPrescription(prescription);
            }
        }
        return prescriptionRepository.save(prescription);
    }

    public Prescription updatePrescription(Long id, Prescription updatedPrescription) {
        Prescription excistingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        excistingPrescription.getMedicines().clear();
        if(updatedPrescription.getMedicines() != null){
            for (PrescriptionItem pi : updatedPrescription.getMedicines()) {
                pi.setPrescription(updatedPrescription);
                excistingPrescription.getMedicines().add(pi);
            }
        }
        return prescriptionRepository.save(excistingPrescription);
    }

    public void deletePrescription(Long id) {
        Prescription excistingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        prescriptionRepository.delete(excistingPrescription);
    }

}
