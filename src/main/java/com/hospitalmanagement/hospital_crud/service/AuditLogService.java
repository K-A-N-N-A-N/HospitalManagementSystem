package com.hospitalmanagement.hospital_crud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import com.hospitalmanagement.hospital_crud.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AuditLog auditLog) {

        try {
            auditLogRepository.save(auditLog);
            log.info("AUDIT: {} - {} [{}]",
                    auditLog.getAction(),
                    auditLog.getEntityName(),
                    auditLog.getEntityId());
        } catch (Exception ex) {
            throw new SystemOperationException(
                    "Failed to save audit record for entity: " + auditLog.getEntityName(), ex);
        }
    }

    public void logLoginEvent(String userId, String username, String role) {

        AuditLog audit = new AuditLog();
        audit.setEntityName("User");
        audit.setEntityId(userId);
        audit.setAction("LOGIN");
        audit.setPerformedBy(username);
        audit.setRole(role);

        audit.setChanges(toJson(Map.of(
                "message", "User logged in",
                "userId", userId,
                "username", username,
                "role", role
        )));

        save(audit);
    }


    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new SystemOperationException(
                    "Failed to serialize audit data for: " + obj.getClass().getSimpleName(), ex);
        }
    }
}
