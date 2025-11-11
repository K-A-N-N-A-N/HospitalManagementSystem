package com.hospitalmanagement.hospital_crud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.exceptions.AuditSaveException;
import com.hospitalmanagement.hospital_crud.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public void save(AuditLog auditLog) {

        if (auditLog.getId() == null) {
            auditLog.setId(UUID.randomUUID().toString());
        }

        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(LocalDateTime.now());
        }

        try {
            auditLogRepository.save(auditLog);
            log.info("AUDIT: {} - {} [{}]",
                    auditLog.getAction(),
                    auditLog.getEntityName(),
                    auditLog.getEntityId());
        } catch (Exception ex) {
            throw new AuditSaveException(
                    "Failed to save audit record for entity: " + auditLog.getEntityName(), ex);
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new AuditSaveException(
                    "Failed to serialize audit data for: " + obj.getClass().getSimpleName(), ex);
        }
    }
}
