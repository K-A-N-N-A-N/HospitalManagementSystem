package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.PatientVisitHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientVisitHistoryRepository extends JpaRepository<PatientVisitHistory, Long> {
    List<PatientVisitHistory> findByPatientId(Long patientId);
}
