package com.hospitalmanagement.hospital_crud.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(name = "entity_name")
    private String entityName;
    @Column(name = "entity_id")
    private String entityId;

    private String action;
    @Column(name = "performed_by")
    private String performedBy;
    private String role;

    @Column(columnDefinition = "TEXT")
    private String changes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
