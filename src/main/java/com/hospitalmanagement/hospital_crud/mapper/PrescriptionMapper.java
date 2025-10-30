package com.hospitalmanagement.hospital_crud.mapper;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;

import java.util.stream.Collectors;

public class PrescriptionMapper {

    public static PrescriptionDTO toDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setCreatedAt(prescription.getCreatedAt());
        dto.setUpdatedAt(prescription.getUpdatedAt());
        dto.setAppointment_id(prescription.getAppointment().getId());

        if (prescription.getMedicines() != null) {
            dto.setMedicines(
                    prescription.getMedicines().stream().map(item -> {
                        PrescriptionItemDTO itemDTO = new PrescriptionItemDTO();
                        itemDTO.setId(item.getId());
                        itemDTO.setMedicineName(item.getMedicineName());
                        itemDTO.setDosage(item.getDosage());
                        itemDTO.setNotes(item.getNotes());
                        return itemDTO;
                    }).collect(Collectors.toList())
            );
        }
        return dto;
    }

    public static Prescription toEntity(PrescriptionDTO dto) {
        Prescription prescription = new Prescription();
        prescription.setId(dto.getId());
        prescription.setCreatedAt(dto.getCreatedAt());
        prescription.setUpdatedAt(dto.getUpdatedAt());

        //Convert DTO appointment_id -> Appointment Refrence
        Appointment appointment = new Appointment();
        appointment.setId(dto.getAppointment_id());
        prescription.setAppointment(appointment);

        if (dto.getMedicines() != null) {
            prescription.setMedicines(
                    dto.getMedicines().stream().map(itemDTO -> {
                        PrescriptionItem item = new PrescriptionItem();
                        item.setId(itemDTO.getId());
                        item.setMedicineName(itemDTO.getMedicineName());
                        item.setDosage(itemDTO.getDosage());
                        item.setNotes(itemDTO.getNotes());
                        item.setPrescription(prescription); // important for relationship
                        return item;
                    }).collect(Collectors.toList())
            );
        }
        return prescription;
    }
}
