package com.hospitalmanagement.hospital_crud.exceptions;

public class AuditSaveException extends RuntimeException {
    public AuditSaveException(String message) {
        super(message);
    }

    public AuditSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
