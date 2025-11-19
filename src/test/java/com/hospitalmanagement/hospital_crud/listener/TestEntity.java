package com.hospitalmanagement.hospital_crud.listener;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.Data;

@Data
@Entity
public class TestEntity {
    @Id
    private String id;
    private String name;
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private TestEntity parent;
}
