package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByIdAndActiveTrue(Long patientId);
    List<Patient> findByActiveFalse();
    List<Patient> findByActiveTrue();

}
