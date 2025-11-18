package com.hospitalmanagement.hospital_crud.controller;

import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.exceptions.GlobalExceptionHandler;
import com.hospitalmanagement.hospital_crud.repository.AuditLogRepository;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogController auditLogController;

    AuditLog log1;
    AuditLog log2;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(auditLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        log1 = new AuditLog();
        log1.setId("L1");
        log1.setEntityName("Doctor");
        log1.setEntityId("D1");
        log1.setAction("CREATE");
        log1.setCreatedAt(Instant.now());

        log2 = new AuditLog();
        log2.setId("L2");
        log2.setEntityName("Patient");
        log2.setEntityId("P1");
        log2.setAction("UPDATE");
        log2.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("GET /api/audit-logs - Should return paged logs")
    void getAllLogs_shouldReturnPagedLogs() throws Exception {

        PageRequest request = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = new PageImpl<>(List.of(log1, log2), request, 2);

        when(auditLogRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/audit-logs")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("L1"))
                .andExpect(jsonPath("$.content[1].id").value("L2"));

        verify(auditLogRepository).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("GET /api/audit-logs/{id} - Should return audit log when ID exists")
    void getAuditLogById_shouldReturnLog() throws Exception {

        when(auditLogRepository.findById("L1")).thenReturn(Optional.of(log1));

        mockMvc.perform(get("/api/audit-logs/L1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("L1"))
                .andExpect(jsonPath("$.entityName").value("Doctor"));

        verify(auditLogRepository).findById("L1");
    }

    @Test
    @DisplayName("GET /api/audit-logs/{id} - Should return NOT_FOUND when audit log does NOT exist")
    void getAuditLogById_shouldReturnNotFound() throws Exception {

        when(auditLogRepository.findById("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/audit-logs/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Audit log not found"));

        verify(auditLogRepository).findById("Unknown");
    }

    @Test
    @DisplayName("GET /api/audit-logs/entity/{entityName} - Should filter logs by entity name")
    void getLogsByEntityName_shouldReturnFiltered() throws Exception {

        PageRequest request = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = new PageImpl<>(List.of(log1, log2), request, 2);

        when(auditLogRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/audit-logs/entity/Doctor")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].entityName").value("Doctor"));

        verify(auditLogRepository).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("GET /api/audit-logs/entity/{entityName} - Should return EMPTY list when no matching logs found")
    void getLogsByEntityName_shouldReturnEmpty() throws Exception {

        PageRequest request = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = new PageImpl<>(List.of(log1, log2), request, 2);

        when(auditLogRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/audit-logs/entity/Admin")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0));

        verify(auditLogRepository).findAll(any(PageRequest.class));
    }
}
