package com.santosfv.purchases.repository;

import com.santosfv.purchases.controllers.PurchaseRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "purchases")
public class PurchaseModel {
    @Id
    private final UUID id;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 50, message = "Description must not exceed 50 characters")
    @Column(name = "description", length = 50)
    private final String description;

    @NotNull(message = "Transaction date cannot be null")
    @Column(name = "transaction_date")
    private final LocalDateTime transactionDate;

    @NotNull(message = "Purchase amount cannot be null")
    @Positive(message = "Purchase amount must be positive")
    @Column(name = "amount", precision = 10, scale = 2)
    private final BigDecimal amount;

    // Default constructor for JPA
    @SuppressWarnings("unused")
    PurchaseModel() {
        this(null, null, null, BigDecimal.ZERO);
    }

    public PurchaseModel(UUID id, String description, LocalDateTime transactionDate, BigDecimal amount) {
        this.id = id != null ? id : UUID.randomUUID();
        this.description = description;
        this.transactionDate = transactionDate;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseModel model = (PurchaseModel) o;
        return Objects.equals(id, model.id)
                && Objects.equals(description, model.description)
                && Objects.equals(transactionDate, model.transactionDate)
                && Objects.equals(amount, model.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, transactionDate, amount);
    }

    public static PurchaseModel from(PurchaseRequest purchase) {
        return new PurchaseModel(
                UUID.randomUUID(),
                purchase.description(),
                purchase.transactionDate(),
                purchase.amount()
        );
    }
}