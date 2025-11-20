package com.hospitalmanagement.hospital_crud.dto;

import com.hospitalmanagement.hospital_crud.entity.Appointment;
import com.hospitalmanagement.hospital_crud.entity.AppointmentStatus;
import com.hospitalmanagement.hospital_crud.entity.Doctor;
import com.hospitalmanagement.hospital_crud.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentResponseTest {

    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId("D1");

        patient = new Patient();
        patient.setId("P1");

        appointment = new Appointment();
        appointment.setId("A1");
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        appointment.setReason("Follow-up");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Mapping constructor should map fields from Appointment entity correctly")
    void constructor_shouldMapAppointmentToResponse() {
        AppointmentResponse response = new AppointmentResponse(appointment);

        assertEquals("A1", response.getId());
        assertEquals("D1", response.getDoctorId());
        assertEquals("P1", response.getPatientId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 30), response.getAppointmentTime());
        assertEquals("Follow-up", response.getReason());
        assertEquals("SCHEDULED", response.getStatus());
    }

    @Test
    @DisplayName("Mapping constructor should handle null doctor and patient safely")
    void constructor_shouldHandleNullAssociations() {
        appointment.setDoctor(null);
        appointment.setPatient(null);

        AppointmentResponse response = new AppointmentResponse(appointment);

        assertNull(response.getDoctorId());
        assertNull(response.getPatientId());
    }

    @Test
    @DisplayName("Mapping constructor should set status to null when appointment status is null")
    void constructor_shouldSetStatusToNull_whenStatusIsNull() {
        appointment.setStatus(null);

        AppointmentResponse response = new AppointmentResponse(appointment);

        assertNull(response.getStatus());
    }

    @Test
    @DisplayName("Setter and getter functions should work correctly")
    void settersAndGetters_shouldWork() {
        AppointmentResponse dto = new AppointmentResponse();

        dto.setId("A1");
        dto.setDoctorId("D1");
        dto.setPatientId("P1");
        dto.setAppointmentTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        dto.setReason("Test");
        dto.setStatus("COMPLETED");

        assertEquals("A1", dto.getId());
        assertEquals("D1", dto.getDoctorId());
        assertEquals("P1", dto.getPatientId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 30), dto.getAppointmentTime());
        assertEquals("Test", dto.getReason());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    @DisplayName("Two DTOs with same data should be equal")
    void dto_equalsShouldReturnTrue() {
        AppointmentResponse dto1 = new AppointmentResponse("A1", "D1", "P1",
                LocalDateTime.of(2025, 1, 1, 10, 30), "Follow-up", "SCHEDULED");

        AppointmentResponse dto2 = new AppointmentResponse("A1", "D1", "P1",
                LocalDateTime.of(2025, 1, 1, 10, 30), "Follow-up", "SCHEDULED");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("toString should not return null or blank")
    void dto_toStringShouldBeValid() {
        AppointmentResponse response = new AppointmentResponse(appointment);

        assertNotNull(response.toString());
        assertFalse(response.toString().isBlank());
    }
}
