package com.hospitalmanagement.hospital_crud.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class PharmacyIntegrationService {

    private final WebClient muleClient;

    public PharmacyIntegrationService(WebClient muleClient) {
        this.muleClient = muleClient;
    }

    public List<Map<String, Object>> getAllMedicines() {
        return muleClient.get()
                .uri("/pharmacy/medicines/public/all")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public Map<String, Object> getLiteMedicine(String sku) {
        return muleClient.get()
                .uri("/pharmacy/medicines/sku/{sku}/lite", sku)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> validatePrescription(Map<String, Object> payload) {
        return muleClient.post()
                .uri("/pharmacy/validate-prescription")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
