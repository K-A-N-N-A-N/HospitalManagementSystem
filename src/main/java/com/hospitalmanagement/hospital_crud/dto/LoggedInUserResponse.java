package com.hospitalmanagement.hospital_crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoggedInUserResponse {
    private String id;
    private String username;
    private String role;
    private String doctorId;
    private String patientId;
}