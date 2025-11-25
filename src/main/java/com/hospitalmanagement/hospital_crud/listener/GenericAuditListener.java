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

import org.springframework.security.core.context.SecurityContextHolder;

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
            String username = getCurrentUsername();
            String role = getCurrentRole();

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
            auditLog.setPerformedBy(username);
            auditLog.setRole(role);
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
            // Un proxy first
            Object unproxied = org.hibernate.Hibernate.unproxy(obj);

            // Remove all @ManyToOne / @OneToMany / @OneToOne / @ManyToMany lazy fields
            Object sanitized = sanitizeEntity(unproxied);

            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return mapper.convertValue(sanitized, new TypeReference<>() {});
        } catch (Exception ex) {
            log.error("Failed to convert object to map for auditing: {}", ex.getMessage());
            return Collections.emptyMap();
        }
    }

    private Object sanitizeEntity(Object entity) {
        try {
            Class<?> clazz = entity.getClass();
            Object copy = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // Skip lazy relationships
                if (field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) ||
                        field.isAnnotationPresent(jakarta.persistence.OneToOne.class) ||
                        field.isAnnotationPresent(jakarta.persistence.OneToMany.class) ||
                        field.isAnnotationPresent(jakarta.persistence.ManyToMany.class)) {
                    continue; // DO NOT touch relations
                }

                // Copy simple fields
                field.set(copy, field.get(entity));
            }

            return copy;

        } catch (Exception e) {
            return entity; // fallback
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
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("before", oldVal);
                changeMap.put("after", newVal);
                diff.put(key, changeMap);
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

    private String getCurrentUsername() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
                return "SYSTEM";
            }
            return auth.getName();
        } catch (Exception e) {
            return "SYSTEM";
        }
    }

    private String getCurrentRole() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getAuthorities() == null) {
                return null;
            }

            return auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

}
