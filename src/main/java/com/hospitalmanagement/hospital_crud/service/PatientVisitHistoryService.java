package com.hospitalmanagement.hospital_crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospitalmanagement.hospital_crud.dto.AppointmentSummaryDTO;
import com.hospitalmanagement.hospital_crud.dto.PatientVisitHistoryDTO;
import com.hospitalmanagement.hospital_crud.entity.PatientVisitHistory;
import com.hospitalmanagement.hospital_crud.exceptions.ResourceNotFoundException;
import com.hospitalmanagement.hospital_crud.repository.PatientVisitHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientVisitHistoryService {

    private final PatientVisitHistoryRepository patientVisitHistoryRepository;
    private final ObjectMapper objectMapper;

    public PatientVisitHistoryService(PatientVisitHistoryRepository patientVisitHistoryRepository) {
        this.patientVisitHistoryRepository = patientVisitHistoryRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void recordPatientVisitHistory(AppointmentSummaryDTO appointmentSummaryDTO) {
        try {
            String jsonSummary = objectMapper.writeValueAsString(appointmentSummaryDTO);

            PatientVisitHistory patientVisitHistory = PatientVisitHistory.builder()
                    .patientId(appointmentSummaryDTO.getPatientId())
                    .patientName(appointmentSummaryDTO.getPatientName())
                    .appointmentId(appointmentSummaryDTO.getAppointmentId())
                    .visitDate(appointmentSummaryDTO.getAppointmentDate())
                    .appointmentSummaryJson(jsonSummary)
                    .build();

            patientVisitHistoryRepository.save(patientVisitHistory);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing appointment summary: " + e.getOriginalMessage());
        }
    }

    public List<PatientVisitHistoryDTO> getVisitHistoryByPatient(Long patientId) {
        List<PatientVisitHistory> patientVisitHistory = patientVisitHistoryRepository.findByPatientId(patientId);

        if (patientVisitHistory.isEmpty()) {
            throw new ResourceNotFoundException("No visit history found for patient");
        }

        return patientVisitHistory.stream()
                .map(history -> PatientVisitHistoryDTO.builder()
                        .id(history.getId())
                        .patientId(history.getPatientId())
                        .patientName(history.getPatientName())
                        .visitDate(history.getVisitDate())
                        .appointmentSummary(deserializeSummary(history.getAppointmentSummaryJson()))
                        .build())
                .collect(Collectors.toList());
    }

    private AppointmentSummaryDTO deserializeSummary(String json) {
        try {
            return objectMapper.readValue(json, AppointmentSummaryDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing visit summary JSON: " + e.getOriginalMessage());
        }
    }
}
