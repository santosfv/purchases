package com.santosfv.purchases.controllers;

import com.santosfv.purchases.PurchaseService;
import com.santosfv.purchases.repository.PurchaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchasesController {

    private final PurchaseService purchases;

    @Autowired
    PurchasesController(PurchaseService purchaseService) {
        this.purchases = purchaseService;
    }

    @PostMapping
    public ResponseEntity<Purchase> create(@RequestBody PurchaseRequest purchaseRequest) {
        PurchaseModel purchase = purchases.createPurchase(purchaseRequest);
        return ResponseEntity.
            status(HttpStatus.CREATED)
            .body(Purchase.from(purchase));
    }

    @GetMapping
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getAllPurchases() {
        Map<String, Object> purchase1 = new HashMap<>();
        purchase1.put("id", "12345");
        purchase1.put("amount", 100.50);
        purchase1.put("description", "First purchase");

        Map<String, Object> purchase2 = new HashMap<>();
        purchase2.put("id", "67890");
        purchase2.put("amount", 200.75);
        purchase2.put("description", "Second purchase");

        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("purchases", Arrays.asList(purchase1, purchase2));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "up");
        response.put("message", "PurchasesController is up and running");
        return ResponseEntity.ok(response);
    }
}