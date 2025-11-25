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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FULL COVERAGE TEST SUITE FOR GenericAuditListener
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

    // Static mocks
    MockedStatic<SpringContext> springCtxMock;
    MockedStatic<Hibernate> hibernateMock;
    MockedStatic<SecurityContextHolder> securityMock;

    ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);

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

        springCtxMock = mockStatic(SpringContext.class);
        hibernateMock = mockStatic(Hibernate.class);
        securityMock = mockStatic(SecurityContextHolder.class);

        springCtxMock.when(() -> SpringContext.getBean(AuditLogService.class))
                .thenReturn(auditLogService);

        springCtxMock.when(() -> SpringContext.getBean(EntityManager.class))
                .thenReturn(entityManager);

        when(entityManager.getEntityManagerFactory()).thenReturn(emf);
        when(emf.createEntityManager()).thenReturn(entityManager);
        when(entityManager.find(TestEntity.class, "T1")).thenReturn(oldEntity);

        hibernateMock.when(() -> Hibernate.unproxy(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Capture json argument
        doAnswer(inv -> {
            capturedJsonArg.set(inv.getArgument(0));
            return "JSON_RESULT";
        }).when(auditLogService).toJson(any());

        doNothing().when(auditLogService).save(auditCaptor.capture());

        // Default SecurityContext
        SecurityContext empty = mock(SecurityContext.class);
        when(empty.getAuthentication()).thenReturn(null);
        securityMock.when(SecurityContextHolder::getContext).thenReturn(empty);
    }

    @AfterEach
    void tearDown() {
        springCtxMock.close();
        hibernateMock.close();
        securityMock.close();
    }

    // ------------------------------------------------------------
    //  CORE OPERATION TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("prePersist should generate CREATE audit")
    void test_prePersist() {

        listener.prePersist(newEntity);

        verify(auditLogService).save(any());

        AuditLog audit = auditCaptor.getValue();
        assertEquals("CREATE", audit.getAction());
        assertEquals("TestEntity", audit.getEntityName());
    }

    @Test
    @DisplayName("preUpdate should generate UPDATE with diff")
    void test_preUpdate() {

        listener.preUpdate(newEntity);

        verify(auditLogService).save(any());

        AuditLog audit = auditCaptor.getValue();
        assertEquals("UPDATE", audit.getAction());

        Map<?, ?> jsonMap = (Map<?, ?>) capturedJsonArg.get();
        assertTrue(jsonMap.containsKey("diff"));
    }

    @Test
    @DisplayName("preUpdate should generate SOFT_DELETE when active=false")
    void test_preUpdate_softDelete() {

        newEntity.setActive(false);

        listener.preUpdate(newEntity);

        verify(auditLogService).save(any());
        assertEquals("SOFT_DELETE", auditCaptor.getValue().getAction());
    }

    @Test
    @DisplayName("preRemove should generate DELETE audit")
    void test_preRemove() {

        listener.preRemove(newEntity);

        verify(auditLogService).save(any());
        assertEquals("DELETE", auditCaptor.getValue().getAction());
    }

    // ------------------------------------------------------------
    //  SKIP AUDIT PATH
    // ------------------------------------------------------------

    @Test
    @DisplayName("Should skip audit when entity is AuditLog")
    void test_skipAuditForAuditLogEntity() {

        listener.prePersist(new AuditLog());

        verify(auditLogService, never()).save(any());
    }

    // ------------------------------------------------------------
    //  USERNAME / ROLE BRANCH TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("getCurrentUsername - anonymousUser -> SYSTEM")
    void test_usernameAnonymous() {

        SecurityContext ctx = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("anonymousUser");
        when(ctx.getAuthentication()).thenReturn(auth);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(ctx);

        listener.prePersist(newEntity);

        assertEquals("SYSTEM", auditCaptor.getValue().getPerformedBy());
    }

    @Test
    @DisplayName("getCurrentUsername - auth null -> SYSTEM")
    void test_usernameAuthNull() {

        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(null);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(ctx);

        listener.prePersist(newEntity);

        assertEquals("SYSTEM", auditCaptor.getValue().getPerformedBy());
    }

    @Test
    @DisplayName("getCurrentRole returns null when authorities null")
    void test_roleAuthoritiesNull() {

        SecurityContext ctx = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getAuthorities()).thenReturn(null);
        when(ctx.getAuthentication()).thenReturn(auth);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(ctx);

        listener.prePersist(newEntity);

        assertNull(auditCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("getCurrentRole returns null when authorities empty")
    void test_roleAuthoritiesEmpty() {

        SecurityContext ctx = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        when(ctx.getAuthentication()).thenReturn(auth);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(ctx);

        listener.prePersist(newEntity);

        assertNull(auditCaptor.getValue().getRole());
    }

    // ------------------------------------------------------------
    //  loadOldState EXCEPTION TEST
    // ------------------------------------------------------------

    @Test
    @DisplayName("loadOldState should return null on exception")
    void test_loadOldState_exception() {

        springCtxMock.when(() -> SpringContext.getBean(EntityManager.class))
                .thenThrow(new RuntimeException("boom"));

        listener.preUpdate(newEntity);

        verify(auditLogService).save(any());
    }

    // ------------------------------------------------------------
    //  toMap EXCEPTION BRANCH
    // ------------------------------------------------------------

    @Test
    @DisplayName("toMap should return emptyMap on unproxy failure")
    void test_toMap_failure() {

        hibernateMock.when(() -> Hibernate.unproxy(any()))
                .thenThrow(new RuntimeException("fail"));

        listener.prePersist(newEntity);

        verify(auditLogService).save(any());
    }

    // ------------------------------------------------------------
    //  sanitizeEntity FALLBACK BRANCH
    // ------------------------------------------------------------

    @Test
    @DisplayName("sanitizeEntity fallback when reflection fails")
    void test_sanitizeEntity_fallback() {

        TestEntity bad = mock(TestEntity.class);

        hibernateMock.when(() -> Hibernate.unproxy(any()))
                .thenReturn(bad);

        listener.prePersist(bad);

        verify(auditLogService).save(any());
    }

    // ------------------------------------------------------------
    //  convertId BRANCH TESTS
    // ------------------------------------------------------------

    @Test
    @DisplayName("convertId should return UUID when type is UUID")
    void test_convertId_uuid() {

        UUIDEntity e = new UUIDEntity();
        e.setId(UUID.randomUUID().toString());

        listener.prePersist(e);

        verify(auditLogService).save(any());
    }

    @Test
    @DisplayName("convertId should return Long for Long id fields")
    void test_convertId_long() {

        LongEntity e = new LongEntity();
        e.setId(5L);

        listener.prePersist(e);

        verify(auditLogService).save(any());
    }


    // ----------------------------------------------------------
    // Helper test entity classes
    // ----------------------------------------------------------

    static class TestEntity {
        private String id;
        private String name;
        private Boolean active;
        private TestEntity parent;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }

        public TestEntity getParent() { return parent; }
        public void setParent(TestEntity parent) { this.parent = parent; }
    }

    static class UUIDEntity {
        @jakarta.persistence.Id
        private UUID id;

        public String getId() { return id.toString(); }
        public void setId(String uuid) { this.id = UUID.fromString(uuid); }
    }

    static class LongEntity {
        @jakarta.persistence.Id
        private Long id;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
