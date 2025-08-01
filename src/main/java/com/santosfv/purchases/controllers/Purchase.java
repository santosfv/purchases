package com.santosfv.purchases.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.santosfv.purchases.repository.PurchaseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Purchase(
        @JsonProperty("id")
        @NotNull(message = "ID cannot be null")
        UUID id,

        @JsonProperty("description")
        @NotBlank(message = "Description cannot be blank")
        @Size(max = 50, message = "Description must not exceed 50 characters")
        String description,

        @JsonProperty("transactionDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull(message = "Transaction date cannot be null")
        LocalDateTime transactionDate,

        @JsonProperty("amount")
        @NotNull(message = "Purchase amount cannot be null")
        @Positive(message = "Purchase amount must be positive")
        BigDecimal amount
) {
    public static Purchase from(PurchaseModel model) {
        return new Purchase(
                model.getId(),
                model.getDescription(),
                model.getTransactionDate(),
                model.getAmount()
        );
    }
}