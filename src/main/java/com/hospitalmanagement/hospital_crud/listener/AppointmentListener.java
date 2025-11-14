package com.hospitalmanagement.hospital_crud.listener;

import com.hospitalmanagement.hospital_crud.dto.AppointmentMessageDTO;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import com.hospitalmanagement.hospital_crud.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentListener {

    private final AppointmentRepository appointmentRepository;
    private final DoctorSlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final EmailService emailService;

    public AppointmentListener(
            AppointmentRepository appointmentRepository,
            DoctorSlotRepository slotRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.emailService = emailService;
    }

    @Transactional
    @JmsListener(destination = "appointment.queue")
    public void handleAppointmentCreate(AppointmentMessageDTO msg) {

        try {
            Doctor doctor = doctorRepository.findById(msg.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            Patient patient = patientRepository.findById(msg.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            DoctorSlot slot = slotRepository.findById(msg.getSlotId())
                    .orElseThrow(() -> new RuntimeException("Slot not found"));

            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setSlot(slot);
            appointment.setAppointmentTime(msg.getAppointmentTime());
            appointment.setReason(msg.getReason());
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            appointmentRepository.save(appointment);

            emailService.sendAppointmentNotification(
                    doctor.getEmail(),
                    doctor.getName(),
                    patient.getName(),
                    msg.getAppointmentTime().toString()
            );

            log.info("Appointment created via queue for doctor {}", doctor.getName());

        } catch (Exception e) {
            log.error("Failed to process appointment: {}", e.getMessage());
        }
    }

}
