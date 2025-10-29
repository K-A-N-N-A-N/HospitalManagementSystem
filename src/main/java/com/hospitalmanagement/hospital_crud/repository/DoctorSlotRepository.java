package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Long> {

    List<DoctorSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    @Query("SELECT s FROM DoctorSlot s WHERE s.doctor.id = :doctorId AND s.date = :date AND s.available = true")
    List<DoctorSlot> findAvailableSlots(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("""
        SELECT s FROM DoctorSlot s
        WHERE s.doctor.id = :doctorId
          AND s.date = :date
          AND s.startTime <= :time
          AND :time < s.endTime
          AND s.available = true
        """)
    Optional<DoctorSlot> findAvailableSlotForDoctorAtTime(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);

    // Get a Specific Slot for a doctor on a given Date
    Optional<DoctorSlot> findByDoctorIdAndDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);

}
