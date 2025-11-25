package com.hospitalmanagement.hospital_crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import com.hospitalmanagement.hospital_crud.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    AuditLogRepository auditLogRepository;

    @InjectMocks
    AuditLogService auditLogService;

    AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        auditLog.setEntityName("Patient");
        auditLog.setEntityId("P1");
        auditLog.setAction("CREATE");
        auditLog.setPerformedBy("tester");
        auditLog.setRole("ADMIN");
        auditLog.setChanges("{\"name\":\"John\"}");
    }

    @Test
    @DisplayName("Should save audit log successfully")
    void save_shouldPersistAuditLog() {
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);

        assertDoesNotThrow(() -> auditLogService.save(auditLog));

        verify(auditLogRepository, times(1)).save(auditLog);
    }

    @Test
    @DisplayName("Should throw SystemOperationException when repository save fails")
    void save_shouldThrowSystemOperationException_whenRepositoryFails() {
        when(auditLogRepository.save(auditLog)).thenThrow(new RuntimeException("DB error"));

        SystemOperationException exception =
                assertThrows(SystemOperationException.class, () -> auditLogService.save(auditLog));

        assertTrue(exception.getMessage().contains("Failed to save audit record"));
        verify(auditLogRepository, times(1)).save(auditLog);
    }

    @Test
    @DisplayName("Should convert object to JSON string")
    void toJson_shouldSerializeObject() {
        TestPayload payload = new TestPayload("value");

        String json = auditLogService.toJson(payload);

        assertTrue(json.contains("\"data\":\"value\""));
    }

    @Test
    @DisplayName("Should throw SystemOperationException when serialization fails")
    void toJson_shouldThrowSystemOperationException_whenSerializationFails() throws Exception {
        AuditLogService serviceWithFailingMapper = new AuditLogService(auditLogRepository);
        Field mapperField = AuditLogService.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);

        ObjectMapper failingMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("forced failure") {};
            }
        };

        mapperField.set(serviceWithFailingMapper, failingMapper);

        TestPayload payload = new TestPayload("value");

        SystemOperationException exception =
                assertThrows(SystemOperationException.class, () -> serviceWithFailingMapper.toJson(payload));

        assertTrue(exception.getMessage().contains("Failed to serialize audit data"));
    }

    @Test
    @DisplayName("Should create and save audit log for login event")
    void logLoginEvent_shouldSaveAuditLog() {

        // We capture the AuditLog object passed to repository
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        auditLogService.logLoginEvent("U1", "john", "ADMIN");

        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog saved = captor.getValue();

        assertEquals("User", saved.getEntityName());
        assertEquals("U1", saved.getEntityId());
        assertEquals("LOGIN", saved.getAction());
        assertEquals("john", saved.getPerformedBy());
        assertEquals("ADMIN", saved.getRole());

        // Ensure JSON changes are correctly created
        assertTrue(saved.getChanges().contains("\"message\":\"User logged in\""));
        assertTrue(saved.getChanges().contains("\"userId\":\"U1\""));
        assertTrue(saved.getChanges().contains("\"username\":\"john\""));
        assertTrue(saved.getChanges().contains("\"role\":\"ADMIN\""));
    }

    @Test
    @DisplayName("Should throw SystemOperationException when logLoginEvent fails to save")
    void logLoginEvent_shouldThrow_whenSaveFails() {

        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("DB down"));

        SystemOperationException ex = assertThrows(
                SystemOperationException.class,
                () -> auditLogService.logLoginEvent("U1", "john", "ADMIN")
        );

        assertTrue(ex.getMessage().contains("Failed to save audit record"));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }


    private record TestPayload(String data) {}
}

