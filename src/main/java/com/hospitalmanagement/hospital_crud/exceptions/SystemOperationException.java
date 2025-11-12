package com.hospitalmanagement.hospital_crud.exceptions;

/**
 * A universal exception for system or external operation failures.
 * Use this when an unexpected error occurs in external integrations
 * like messaging (ActiveMQ/Kafka), audit logging, mail service, etc.
 */
public class SystemOperationException extends RuntimeException {

    public SystemOperationException(String message) {
        super(message);
    }

    public SystemOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
