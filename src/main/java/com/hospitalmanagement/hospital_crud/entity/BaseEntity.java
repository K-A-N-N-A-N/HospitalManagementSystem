package com.hospitalmanagement.hospital_crud.entity;

import com.hospitalmanagement.hospital_crud.listener.GenericAuditListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity class that automatically enables auditing for
 * all subclasses (Doctor, Patient, Appointment, etc.)
 */

@MappedSuperclass
@EntityListeners(GenericAuditListener.class)
@Getter
@Setter
public abstract class BaseEntity {
}
