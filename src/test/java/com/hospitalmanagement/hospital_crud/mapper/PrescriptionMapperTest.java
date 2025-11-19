package com.hospitalmanagement.hospital_crud.mapper;

import com.hospitalmanagement.hospital_crud.dto.PrescriptionDTO;
import com.hospitalmanagement.hospital_crud.dto.PrescriptionItemDTO;
import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.Prescription;
import com.hospitalmanagement.hospital_crud.entity.PrescriptionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionMapperTest {

    Prescription prescription;
    PrescriptionItem item1;
    PrescriptionItem item2;
    Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
        appointment.setId("A1");

        item1 = new PrescriptionItem();
        item1.setId("M1");
        item1.setMedicineName("Paracetamol");
        item1.setDosage("500mg");
        item1.setNotes("Take twice");

        item2 = new PrescriptionItem();
        item2.setId("M2");
        item2.setMedicineName("Ibuprofen");
        item2.setDosage("200mg");
        item2.setNotes("After food");

        prescription = new Prescription();
        prescription.setId("P1");
        prescription.setCreatedAt(Instant.now());
        prescription.setUpdatedAt(Instant.now());
        prescription.setAppointment(appointment);
        prescription.setMedicines(List.of(item1, item2));
    }

    @Test
    @DisplayName("toDTO should map Prescription → PrescriptionDTO correctly")
    void toDTO_shouldMapCorrectly() {
        PrescriptionDTO dto = PrescriptionMapper.toDTO(prescription);

        assertNotNull(dto);
        assertEquals("P1", dto.getId());
        assertEquals("A1", dto.getAppointment_id());
        assertEquals(2, dto.getMedicines().size());

        PrescriptionItemDTO dto1 = dto.getMedicines().get(0);
        assertEquals("M1", dto1.getId());
        assertEquals("Paracetamol", dto1.getMedicineName());
        assertEquals("500mg", dto1.getDosage());
        assertEquals("Take twice", dto1.getNotes());
    }

    @Test
    @DisplayName("toDTO should handle null medicine list gracefully")
    void toDTO_shouldHandleNullMedicines() {
        prescription.setMedicines(null);

        PrescriptionDTO dto = PrescriptionMapper.toDTO(prescription);

        assertNotNull(dto);
        assertNull(dto.getMedicines());
    }

    @Test
    @DisplayName("toEntity should map PrescriptionDTO → Prescription correctly")
    void toEntity_shouldMapCorrectly() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId("P1");
        dto.setAppointment_id("A1");
        dto.setCreatedAt(prescription.getCreatedAt());
        dto.setUpdatedAt(prescription.getUpdatedAt());

        PrescriptionItemDTO dtoItem1 = new PrescriptionItemDTO();
        dtoItem1.setId("M1");
        dtoItem1.setMedicineName("Paracetamol");
        dtoItem1.setDosage("500mg");
        dtoItem1.setNotes("Take twice");

        PrescriptionItemDTO dtoItem2 = new PrescriptionItemDTO();
        dtoItem2.setId("M2");
        dtoItem2.setMedicineName("Ibuprofen");
        dtoItem2.setDosage("200mg");
        dtoItem2.setNotes("After food");

        dto.setMedicines(List.of(dtoItem1, dtoItem2));

        Prescription entity = PrescriptionMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("P1", entity.getId());
        assertEquals("A1", entity.getAppointment().getId());
        assertEquals(2, entity.getMedicines().size());

        PrescriptionItem item = entity.getMedicines().get(0);
        assertEquals("M1", item.getId());
        assertEquals("Paracetamol", item.getMedicineName());
        assertEquals("500mg", item.getDosage());
        assertEquals("Take twice", item.getNotes());

        // ⚠ Important: PrescriptionItem must reference the parent Prescription
        assertEquals(entity, item.getPrescription());
    }

    @Test
    @DisplayName("toEntity should handle null medicine list gracefully")
    void toEntity_shouldHandleNullMedicines() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId("P1");
        dto.setAppointment_id("A1");
        dto.setMedicines(null);

        Prescription entity = PrescriptionMapper.toEntity(dto);

        assertNotNull(entity);
        assertTrue(entity.getMedicines().isEmpty());
    }
}
