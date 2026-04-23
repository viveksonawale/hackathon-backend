package com.evnova.hackathon_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
    @GetMapping("/check")
    public String HealthCheck(){
        return "Working!";
    }

    @GetMapping("/health")
    public String health() {
        return "Working!";
    }
}
