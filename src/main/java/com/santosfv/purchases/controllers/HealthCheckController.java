package com.santosfv.purchases.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health")
public class HealthCheckController {

    @RequestMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("application is running");
    }
}
