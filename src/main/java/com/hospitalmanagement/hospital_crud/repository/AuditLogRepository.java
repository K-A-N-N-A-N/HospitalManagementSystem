package com.hospitalmanagement.hospital_crud.repository;

import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}
