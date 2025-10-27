package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Long> {

    // Get all slots for a specific doctor on a specific date
    List<DoctorSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    // Future Update: get only available slots for that date
    @Query("SELECT s FROM DoctorSlot s WHERE s.doctor.id = :doctorId AND s.date = :date AND s.available = true")
    List<DoctorSlot> findAvailableSlots(Long doctorId, LocalDate date);
}
