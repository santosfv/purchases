package com.santosfv.purchases.controllers;

import com.santosfv.purchases.PurchaseService;
import com.santosfv.purchases.repository.PurchaseModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchasesController {

    private final PurchaseService purchases;

    @Autowired
    PurchasesController(PurchaseService purchaseService) {
        this.purchases = purchaseService;
    }

    @PostMapping
    public ResponseEntity<Purchase> create(@Valid @RequestBody PurchaseRequest purchaseRequest) {
        PurchaseModel purchase = purchases.createPurchase(purchaseRequest);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(Purchase.from(purchase));
    }

    @GetMapping("/{id}/convert/{currency}")
    public ResponseEntity<PurchaseConversion> convertPurchase(@PathVariable UUID id, @PathVariable String currency) {
        PurchaseConversion response = purchases.convertPurchase(id, currency);
        return ResponseEntity.ok(response);
    }
}