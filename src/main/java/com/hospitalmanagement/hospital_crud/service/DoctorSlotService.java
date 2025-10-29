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

    //Generte Default SLots
    public List<DoctorSlot> generateDefaultSlots(Long docId, LocalDate date) {
        Doctor doctor = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<LocalTime[]> defaultSlots = List.of(
                new LocalTime[]{LocalTime.of(9,0),LocalTime.of(10,0)},
                new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(11, 0)},
                new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(12, 0)},
                new LocalTime[]{LocalTime.of(12, 0), LocalTime.of(13, 0)},
                new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(15, 0)},
                new LocalTime[]{LocalTime.of(15, 0), LocalTime.of(16, 0)},
                new LocalTime[]{LocalTime.of(16, 0), LocalTime.of(17, 0)}
        );

        List<DoctorSlot> slots = defaultSlots.stream()
                .map(t -> DoctorSlot.builder()
                        .doctor(doctor)
                        .date(date)
                        .startTime(t[0])
                        .endTime(t[1])
                        .available(true)
                        .build()
                )
                .collect(Collectors.toList());

        return slotRepo.saveAll(slots);
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
