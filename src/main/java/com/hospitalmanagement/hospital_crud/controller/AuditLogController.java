package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    //Get all audit logs with pagination and sorting by creation time (newest first)
    @GetMapping
    public Page<AuditLog> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return auditLogRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    //Get a specific audit log by its ID
    @GetMapping("/{id}")
    public AuditLog getAuditLogById(@PathVariable String id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Audit log not found with id: " + id));
    }

    //Optional: Filter by entity name (e.g., Doctor, Patient)
    @GetMapping("/entity/{entityName}")
    public Page<AuditLog> getLogsByEntityName(
            @PathVariable String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Fetch logs page by page and filter in-memory (simple implementation)
        Page<AuditLog> allLogs = auditLogRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<AuditLog> filtered = allLogs.getContent().stream()
                .filter(log -> log.getEntityName() != null &&
                        log.getEntityName().equalsIgnoreCase(entityName))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, allLogs.getPageable(), filtered.size());
    }
}
