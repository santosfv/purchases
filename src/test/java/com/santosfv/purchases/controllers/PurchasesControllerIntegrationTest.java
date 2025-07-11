package com.santosfv.purchases.controllers;

import com.santosfv.purchases.repository.PurchaseRepository;
import com.santosfv.purchases.treasury.ExchangeRate;
import com.santosfv.purchases.treasury.TreasuryAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SuppressWarnings("DataFlowIssue")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PurchasesControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @MockitoBean
    private TreasuryAPI treasuryAPI;

    private PurchaseRequest purchaseRequest;
    private final LocalDateTime transactionDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        purchaseRepository.deleteAll();

        purchaseRequest = new PurchaseRequest(
                "Test Purchase",
                transactionDate,
                new BigDecimal("100.00")
        );
    }

    @Test
    void shouldCreatePurchase() {
        ResponseEntity<Purchase> response = restTemplate.postForEntity(
                "/api/purchases",
                purchaseRequest,
                Purchase.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals(purchaseRequest.description(), response.getBody().description());
        assertEquals(purchaseRequest.amount(), response.getBody().amount());
    }

    @Test
    void shouldConvertPurchase() {
        // Create a purchase first
        ResponseEntity<Purchase> createResponse = restTemplate.postForEntity(
                "/api/purchases",
                purchaseRequest,
                Purchase.class
        );
        UUID purchaseId = createResponse.getBody().id();
        String currency = "EUR";
        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(treasuryAPI.getLatestExchangeRate(
                eq(currency),
                any(),
                any()
        )).thenReturn(Optional.of(new ExchangeRate(currency, exchangeRate, transactionDate.toLocalDate())));

        ResponseEntity<PurchaseConversion> response = restTemplate.getForEntity(
                "/api/purchases/{id}/convert/{currency}",
                PurchaseConversion.class,
                purchaseId,
                currency
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(purchaseId, response.getBody().id());
        assertEquals(purchaseRequest.description(), response.getBody().description());
        assertEquals(new BigDecimal("85.00"), response.getBody().amount());
        assertEquals(exchangeRate, response.getBody().rate());
        assertEquals(purchaseRequest.amount(), response.getBody().originalAmount());
    }

    @Test
    void shouldReturn404WhenPurchaseNotFound() {
        String currency = "EUR";
        UUID nonExistentId = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/purchases/{id}/convert/{currency}",
                String.class,
                nonExistentId,
                currency
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldValidatePurchaseRequest() {
        PurchaseRequest invalidRequest = new PurchaseRequest(
                "", // Empty description
                transactionDate,
                new BigDecimal("-100.00") // Negative amount
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/purchases",
                invalidRequest,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}