package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.config.SpringContext;
import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.service.AuditLogService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Stable fully corrected test for GenericAuditListener
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GenericAuditListenerTest {

    @InjectMocks
    GenericAuditListener listener;

    @Mock
    AuditLogService auditLogService;

    @Mock
    EntityManagerFactory emf;

    @Mock
    EntityManager entityManager;

    // static mocks
    MockedStatic<SpringContext> springCtxMock;
    MockedStatic<Hibernate> hibernateMock;

    ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);

    // Captures Map passed to auditService.toJson(...)
    AtomicReference<Object> capturedJsonArg = new AtomicReference<>();

    TestEntity newEntity;
    TestEntity oldEntity;

    @BeforeEach
    void setUp() {

        listener = new GenericAuditListener();

        newEntity = new TestEntity();
        newEntity.setId("T1");
        newEntity.setName("New Name");
        newEntity.setActive(true);

        oldEntity = new TestEntity();
        oldEntity.setId("T1");
        oldEntity.setName("Old Name");
        oldEntity.setActive(true);

        // Static mocking SpringContext.getBean(...)
        springCtxMock = mockStatic(SpringContext.class);
        springCtxMock.when(() -> SpringContext.getBean(AuditLogService.class))
                .thenReturn(auditLogService);

        // Mock SpringContext.getBean(EntityManager.class)
        springCtxMock.when(() -> SpringContext.getBean(EntityManager.class))
                .thenReturn(entityManager);

        // Necessary EM chain
        when(entityManager.getEntityManagerFactory()).thenReturn(emf);
        when(emf.createEntityManager()).thenReturn(entityManager);
        when(entityManager.find(TestEntity.class, "T1")).thenReturn(oldEntity);

        // Static mocking Hibernate.unproxy(...)
        hibernateMock = mockStatic(Hibernate.class);
        hibernateMock.when(() -> Hibernate.unproxy(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // intercept toJson so we can inspect the argument
        doAnswer(inv -> {
            capturedJsonArg.set(inv.getArgument(0));
            return "JSON_RESULT";
        }).when(auditLogService).toJson(any());

        doNothing().when(auditLogService).save(auditCaptor.capture());
    }

    @AfterEach
    void cleanup() {
        if (springCtxMock != null) springCtxMock.close();
        if (hibernateMock != null) hibernateMock.close();
    }

    @Test
    @DisplayName("prePersist should generate CREATE audit with after map")
    void test_prePersist() {

        listener.prePersist(newEntity);

        verify(auditLogService, times(1)).save(any(AuditLog.class));

        AuditLog audit = auditCaptor.getValue();
        assertEquals("TestEntity", audit.getEntityName());
        assertEquals("T1", audit.getEntityId());
        assertEquals("CREATE", audit.getAction());

        Object jsonArg = capturedJsonArg.get();
        assertTrue(jsonArg instanceof Map);

        Map<?, ?> map = (Map<?, ?>) jsonArg;
        assertTrue(map.containsKey("after"));

        Map<?, ?> after = (Map<?, ?>) map.get("after");

        assertEquals("New Name", after.get("name"));
        assertTrue((Boolean) after.get("active"));

        // lazy 'parent' must NOT be included
        assertNull(after.get("parent"));
    }

    @Test
    @DisplayName("preUpdate should generate UPDATE audit with before/after/diff")
    void test_preUpdate_normalUpdate() {

        listener.preUpdate(newEntity);

        verify(auditLogService, times(1)).save(any(AuditLog.class));
        AuditLog audit = auditCaptor.getValue();

        assertEquals("UPDATE", audit.getAction());
        assertEquals("TestEntity", audit.getEntityName());
        assertEquals("T1", audit.getEntityId());

        Object jsonArg = capturedJsonArg.get();
        assertTrue(jsonArg instanceof Map);

        Map<?, ?> outer = (Map<?, ?>) jsonArg;

        assertTrue(outer.containsKey("before"));
        assertTrue(outer.containsKey("after"));
        assertTrue(outer.containsKey("diff"));

        Map<?, ?> diffMap = (Map<?, ?>) outer.get("diff");

        // name changed from Old -> New
        assertTrue(diffMap.containsKey("name"));
    }

    @Test
    @DisplayName("preUpdate should generate SOFT_DELETE when active=false")
    void test_preUpdate_softDelete() {

        // soft delete
        newEntity.setActive(false);

        listener.preUpdate(newEntity);

        verify(auditLogService).save(any());

        AuditLog audit = auditCaptor.getValue();
        assertEquals("SOFT_DELETE", audit.getAction());
    }

    @Test
    @DisplayName("preRemove should generate DELETE audit")
    void test_preRemove() {

        listener.preRemove(newEntity);

        verify(auditLogService).save(any());

        AuditLog audit = auditCaptor.getValue();
        assertEquals("DELETE", audit.getAction());
    }
}
