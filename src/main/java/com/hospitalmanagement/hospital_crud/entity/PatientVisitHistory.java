package com.hospitalmanagement.hospital_crud.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVisitHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String patientName;

    private Long appointmentId;
    private LocalDateTime visitDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String appointmentSummaryJson; // Store full summary as JSON for reference
}

