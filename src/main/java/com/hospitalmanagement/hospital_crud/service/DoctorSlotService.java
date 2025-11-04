package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorSlotService {

    private DoctorSlotRepository slotRepo;
    private DoctorRepository docRepo;

    public DoctorSlotService(DoctorSlotRepository slotRepo, DoctorRepository docRepo) {
        this.slotRepo = slotRepo;
        this.docRepo = docRepo;
    }

    // Generate slots with customizable duration
    public List<DoctorSlot> generateSlots(Long docId, LocalDate date, int slotDurationMinutes) {
        Doctor doctor = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + docId));

        if (slotDurationMinutes <= 0 || slotDurationMinutes > 120) {
            throw new IllegalArgumentException("Slot duration must be between 1 and 120 minutes");
        }

        // Define working hours (customize if needed)
        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(17, 0);

        List<DoctorSlot> slots = new java.util.ArrayList<>();

        for (LocalTime time = startOfDay; time.isBefore(endOfDay); time = time.plusMinutes(slotDurationMinutes)) {
            LocalTime endTime = time.plusMinutes(slotDurationMinutes);

            // Don’t go beyond working hours
            if (endTime.isAfter(endOfDay)) break;

            slots.add(DoctorSlot.builder()
                    .doctor(doctor)
                    .date(date)
                    .startTime(time)
                    .endTime(endTime)
                    .available(true)
                    .build());
        }

        return slotRepo.saveAll(slots);
    }

    //get All appointment slots for a given doctor
    public List<DoctorSlot> getAllScheduledSlots(Long docId, LocalDate date) {
        Doctor doctor = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<DoctorSlot> scheduledSlots = slotRepo.findByDoctorIdAndDate(docId, date);

        return scheduledSlots.stream()
                .filter(slot -> !slot.isAvailable())
                .collect(Collectors.toList());
    }

    //get Available Slots for a given doctor (id)
    public List<DoctorSlot> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = docRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<DoctorSlot> allSlots = slotRepo.findByDoctorIdAndDate(doctorId, date);

        return allSlots.stream()
                .filter(DoctorSlot::isAvailable)
                .collect(Collectors.toList());
    }

    // Update the slot to no avialable by the doctor
    public String updateSlotStatus(Long doctorId, LocalDate date, LocalTime startTime, boolean available) {
        Doctor doctor = docRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        DoctorSlot slot = slotRepo.findByDoctorIdAndDateAndStartTime(doctorId, date, startTime)
                .orElseThrow(() -> new RuntimeException(
                        "No slot found for Doctor ID " + doctorId + " on " + date + " at " + startTime));

        slot.setAvailable(available);
        slotRepo.save(slot);

        return "Slot on " + date + " at " + startTime + " for Dr. " + doctor.getName() +
                " updated to available = " + available;
    }


    // Delete all slots for a doctor on a given date
    public String deleteSlotsForDate(Long docId, LocalDate date) {
        Doctor doctor = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Doctor with ID " + docId + " not found"));

        List<DoctorSlot> slots = slotRepo.findByDoctorIdAndDate(docId, date);

        if (slots.isEmpty()) {
            return "No slots found for Doctor " + doctor.getName() + " on " + date;
        }

        slotRepo.deleteAll(slots);
        return "Deleted " + slots.size() + " slots for Doctor " + doctor.getName() + " on " + date;
    }

}
