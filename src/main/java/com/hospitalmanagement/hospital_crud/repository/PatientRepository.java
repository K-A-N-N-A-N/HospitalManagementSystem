package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
