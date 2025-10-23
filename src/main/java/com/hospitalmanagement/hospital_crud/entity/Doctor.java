package com.hospitalmanagement.hospital_crud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.security.Timestamp;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String contactInfo;
    private String photoPath;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and Setters
}
