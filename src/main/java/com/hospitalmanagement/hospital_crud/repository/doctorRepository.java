package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface doctorRepository extends JpaRepository<doctor, Long> {
}
