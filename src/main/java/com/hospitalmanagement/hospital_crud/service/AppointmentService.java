package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.dto.AppointmentRequest;
import com.hospitalmanagement.hospital_crud.entity.*;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.AppointmentRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorRepository;
import com.hospitalmanagement.hospital_crud.repository.DoctorSlotRepository;
import com.hospitalmanagement.hospital_crud.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.hospitalmanagement.hospital_crud.entity.AppointmentStatus.*;

@Service
@Transactional
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private DoctorSlotRepository slotRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorSlotRepository slotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.slotRepository = slotRepository;
    }

    public Appointment createAppointment(AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        LocalDate date = request.getAppointmentTime().toLocalDate();
        LocalTime time = request.getAppointmentTime().toLocalTime();

        // Find Avilable Slots for that specific doctor
        DoctorSlot slot = slotRepository.findAvailableSlotForDoctorAtTime(request.getDoctorId(), date, time)
                .orElseThrow(() -> new RuntimeException("No available slot found for this time"));

        if (!slot.isAvailable()) {
            throw new RuntimeException("Slot not available");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setSlot(slot);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        slot.setAvailable(false);
        slotRepository.save(slot);

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Update doctor if doctorId provided
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found" ));
            existingAppointment.setDoctor(doctor);
        }

        // Update patient if patientId provided
        if (request.getPatientId() != null) {
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            existingAppointment.setPatient(patient);
        }

        // Update appointment time
        if (request.getAppointmentTime() != null) {
            existingAppointment.setAppointmentTime(request.getAppointmentTime());
        }

        // Update reason
        if (request.getReason() != null) {
            existingAppointment.setReason(request.getReason());
        }

        return appointmentRepository.save(existingAppointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAllActiveAppointments() {
        List<AppointmentStatus> activeStatuses = List.of(SCHEDULED, COMPLETED);
        return appointmentRepository.findByStatusIn(activeStatuses);
    }

    public Appointment getAppointmentById(Long id) {
        Appointment excistingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (excistingAppointment.getStatus().equals(CANCELLED)) {
            throw new RuntimeException("Appointment already cancelled");
        }
        return excistingAppointment;
    }

    public String cancelAppointment(Long id) {
        Appointment excistingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (excistingAppointment.getStatus().equals(CANCELLED)) {
                return "Appointment Already cancelled";
        }

        excistingAppointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(excistingAppointment);

        return "Appointment has been sucessfully Cancelled";
    }

    public void deleteAppointment(Long id) {
        Appointment excistingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        DoctorSlot slot = excistingAppointment.getSlot();
        if (slot != null) {
            slot.setAvailable(true);
            slotRepository.save(slot);
        }
        appointmentRepository.deleteById(id);
    }

}
