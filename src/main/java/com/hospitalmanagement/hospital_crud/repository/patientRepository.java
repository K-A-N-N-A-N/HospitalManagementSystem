package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface patientRepository extends JpaRepository<patient, Long> {
}
