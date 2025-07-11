package com.santosfv.purchases.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.santosfv.purchases.repository.PurchaseModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PurchaseConversion(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("description")
        String description,

        @JsonProperty("transactionDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime transactionDate,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("rate")
        BigDecimal rate,

        @JsonProperty("originalAmount")
        BigDecimal originalAmount

){}