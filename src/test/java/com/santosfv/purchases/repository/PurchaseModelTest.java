package com.santosfv.purchases.repository;

import com.santosfv.purchases.controllers.PurchaseRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseModelTest {

    @Test
    void constructorShouldInitializeFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        String description = "Test Purchase";
        LocalDateTime date = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("99.99");

        PurchaseModel subject = new PurchaseModel(id, description, date, amount);

        assertThat(subject.getId()).isEqualTo(id);
        assertThat(subject.getDescription()).isEqualTo(description);
        assertThat(subject.getTransactionDate()).isEqualTo(date);
        assertThat(subject.getAmount()).isEqualTo(amount);
    }

    @Test
    void constructorShouldInitializeAmountRoundedWithTwoDecimal() {
        UUID id = UUID.randomUUID();
        String description = "Test Purchase";
        LocalDateTime date = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("99.999");

        PurchaseModel subject = new PurchaseModel(id, description, date, amount);

        assertThat(subject.getAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void defaultConstructorShouldCreateModelWithNullValues() {
        PurchaseModel subject = new PurchaseModel();

        assertThat(subject.getId()).isNotNull();
        assertThat(subject.getDescription()).isNull();
        assertThat(subject.getTransactionDate()).isNull();
        assertThat(subject.getAmount()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void constructorShouldGenerateIdWhenNull() {
        String description = "Auto ID";
        LocalDateTime date = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("50");

        PurchaseModel subject = new PurchaseModel(null, description, date, amount);

        assertThat(subject.getId()).isNotNull();
    }

    @Test
    void fromMethodShouldCreateModelFromRequest() {
        String description = "From Request";
        LocalDateTime date = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("25.50");

        PurchaseRequest request = new PurchaseRequest(description, date, amount);

        PurchaseModel subject = PurchaseModel.from(request);

        assertThat(subject.getId()).isNotNull();
        assertThat(subject.getDescription()).isEqualTo(description);
        assertThat(subject.getTransactionDate()).isEqualTo(date);
        assertThat(subject.getAmount()).isEqualTo(amount);
    }
}