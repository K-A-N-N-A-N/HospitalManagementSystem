package com.hospitalmanagement.hospital_crud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.security.Timestamp;

@Entity
public class patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String gender;
    private String contactInfo;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and Setters
}
