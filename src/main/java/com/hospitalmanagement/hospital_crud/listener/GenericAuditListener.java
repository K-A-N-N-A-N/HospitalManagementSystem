package com.hospitalmanagement.hospital_crud.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospitalmanagement.hospital_crud.config.SpringContext;
import com.hospitalmanagement.hospital_crud.entity.AuditLog;
import com.hospitalmanagement.hospital_crud.service.AuditLogService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class GenericAuditListener {

    @PrePersist
    public void prePersist(Object entity) {
        createAudit("CREATE", entity, null);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        Object oldState = loadOldState(entity);
        createAudit("UPDATE", entity, oldState);
    }

    @PreRemove
    public void preRemove(Object entity) {
        createAudit("DELETE", entity, null);
    }

    private void createAudit(String action, Object entity, Object oldState) {
        try {
            // Avoid recursive logging (skip AuditLog itself)
            if (entity instanceof AuditLog) {
                return;
            }

            AuditLogService auditService = SpringContext.getBean(AuditLogService.class);

            String entityName = entity.getClass().getSimpleName();
            String entityId = getEntityId(entity);

            // Detect soft delete (if entity has 'active' field and it's false)
            String finalAction = action;
            try {
                Field activeField = entity.getClass().getDeclaredField("active");
                activeField.setAccessible(true);
                Object value = activeField.get(entity);
                if ("UPDATE".equals(action) && value instanceof Boolean && !(Boolean) value) {
                    finalAction = "SOFT_DELETE";
                }
            } catch (NoSuchFieldException ignored) {
                // Entity doesn't have an 'active' field, skip soft delete logic
            }

            String changesJson = generateChangesJson(auditService, entity, oldState);

            AuditLog auditLog = new AuditLog();
            auditLog.setEntityName(entityName);
            auditLog.setEntityId(entityId == null ? "UNKNOWN" : entityId);
            auditLog.setAction(finalAction);
            auditLog.setPerformedBy("SYSTEM"); // default until auth is added
            auditLog.setRole(null);
            auditLog.setChanges(changesJson);

            auditService.save(auditLog);

        } catch (Exception ex) {
            log.error("Failed to create audit log for entity {}", entity.getClass().getSimpleName(), ex);
        }
    }

    private Object loadOldState(Object entity) {
        try {
            String idValue = getEntityId(entity);
            if (idValue == null) return null;

            // Create an entity manager inorder to fetch the "old" version
            EntityManager em = SpringContext.getBean(EntityManager.class)
                    .getEntityManagerFactory()
                    .createEntityManager();

            Class<?> clazz = entity.getClass();
            Object id = convertId(idValue, clazz);

            Object oldState = em.find(clazz, id);
            em.close();
            return oldState;

        } catch (Exception ex) {
            log.warn("Could not load old state for auditing: {}", ex.getMessage());
            return null;
        }
    }

    private String generateChangesJson(AuditLogService auditService, Object newEntity, Object oldEntity) {
        if (oldEntity == null) {
            return auditService.toJson(Map.of("after", toMap(newEntity)));
        }

        Map<String, Object> beforeMap = toMap(oldEntity);
        Map<String, Object> afterMap = toMap(newEntity);
        Map<String, Object> diff = computeDiff(beforeMap, afterMap);

        return auditService.toJson(Map.of(
                "before", beforeMap,
                "after", afterMap,
                "diff", diff
        ));
    }

    private Map<String, Object> toMap(Object obj) {
        try {
            // Use the same configured ObjectMapper from AuditLogService
            AuditLogService auditService = SpringContext.getBean(AuditLogService.class);
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return mapper.convertValue(obj, new TypeReference<>() {});
        } catch (Exception ex) {
            log.error("Failed to convert object to map for auditing: {}", ex.getMessage(), ex);
            return Collections.emptyMap();
        }
    }


    private Map<String, Object> computeDiff(Map<String, Object> before, Map<String, Object> after) {
        Map<String, Object> diff = new HashMap<>();
        Set<String> keys = new HashSet<>();
        keys.addAll(before.keySet());
        keys.addAll(after.keySet());

        for (String key : keys) {
            Object oldVal = before.get(key);
            Object newVal = after.get(key);
            if (!Objects.equals(oldVal, newVal)) {
                diff.put(key, Map.of("before", oldVal, "after", newVal));
            }
        }
        return diff;
    }

    private String getEntityId(Object entity) {
        try {
            // Try getId() method first
            Method getIdMethod = Arrays.stream(entity.getClass().getMethods())
                    .filter(m -> m.getName().equalsIgnoreCase("getId"))
                    .findFirst().orElse(null);

            if (getIdMethod != null) {
                Object id = getIdMethod.invoke(entity);
                return id != null ? id.toString() : null;
            }

            // Fallback: find field annotated with @Id
            Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Id.class))
                    .findFirst().orElse(null);

            if (idField != null) {
                idField.setAccessible(true);
                Object id = idField.get(entity);
                return id != null ? id.toString() : null;
            }
        } catch (Exception ex) {
            log.error("Could not get entity ID: {}", ex.getMessage());
        }
        return null;
    }

    private Object convertId(String idStr, Class<?> entityClass) {
        try {
            Field idField = Arrays.stream(entityClass.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Id.class) || f.getName().equalsIgnoreCase("id"))
                    .findFirst().orElse(null);

            if (idField == null) return idStr;
            Class<?> idType = idField.getType();

            if (idType.equals(UUID.class)) return UUID.fromString(idStr);
            if (idType.equals(Long.class) || idType.equals(long.class)) return Long.valueOf(idStr);
            return idStr;
        } catch (Exception ex) {
            log.warn("Could not convert entity ID: {}", ex.getMessage());
            return idStr;
        }
    }
}
