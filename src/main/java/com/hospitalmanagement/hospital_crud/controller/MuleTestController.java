package com.hospitalmanagement.hospital_crud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mule")
public class MuleTestController {

    private final WebClient webClient;

    public MuleTestController() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping("/test")
    public Mono<String> testMuleConnection() {
        String muleUrl = "http://localhost:8081/testInputValue";

        return webClient.get()
                .uri(muleUrl)
                .retrieve()
                .bodyToMono(String.class);
    }
}
