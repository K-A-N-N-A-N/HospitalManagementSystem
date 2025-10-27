package com.hospitalmanagement.hospital_crud.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class DoctorSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    private LocalTime startTime;
    private LocalTime endTime;

    private boolean available = true;  // Default true

    private LocalDate date; // e.g., 2025-10-28
}
