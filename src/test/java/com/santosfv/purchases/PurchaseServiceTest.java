package com.santosfv.purchases;

import com.santosfv.purchases.controllers.PurchaseConversion;
import com.santosfv.purchases.controllers.PurchaseRequest;
import com.santosfv.purchases.repository.PurchaseModel;
import com.santosfv.purchases.repository.PurchaseRepository;
import com.santosfv.purchases.treasury.ExchangeRate;
import com.santosfv.purchases.treasury.TreasuryAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private TreasuryAPI treasuryAPI;

    @InjectMocks
    private PurchaseService subject;

    private final UUID purchaseId = UUID.randomUUID();
    private PurchaseRequest purchaseRequest;
    private final LocalDateTime transactionDate = LocalDateTime.now();
    private PurchaseModel purchaseModel;

    @BeforeEach
    void setUp() {
        purchaseRequest = new PurchaseRequest(
                "Test Purchase",
                transactionDate,
                new BigDecimal("100.00")
        );

        purchaseModel = new PurchaseModel(
                purchaseId,
                purchaseRequest.description(),
                purchaseRequest.transactionDate(),
                purchaseRequest.amount()
        );
    }

    @Test
    void shouldReturnSavedPurchaseModel() {
        when(purchaseRepository.save(any(PurchaseModel.class))).thenReturn(purchaseModel);

        PurchaseModel result = subject.createPurchase(purchaseRequest);

        assertNotNull(result);
        verify(purchaseRepository).save(any(PurchaseModel.class));
    }

    @Test
    void shouldReturnPurchaseWhenItExists() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchaseModel));

        Optional<PurchaseModel> result = subject.getPurchaseById(purchaseId);

        assertTrue(result.isPresent());
        assertEquals(purchaseModel, result.get());
        verify(purchaseRepository).findById(purchaseId);
    }

    @Test
    void shouldReturnEmptyPurchaseWhenDoNotExists() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        Optional<PurchaseModel> result = subject.getPurchaseById(purchaseId);

        assertTrue(result.isEmpty());
        verify(purchaseRepository).findById(purchaseId);
    }

    @Test
    void shouldReturnConvertedPurchase() {
        String currency = "EUR";
        BigDecimal exchangeRate = new BigDecimal("0.85");
        BigDecimal expectedConvertedAmount = new BigDecimal("85.00");

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchaseModel));
        when(treasuryAPI.getLatestExchangeRate(
                eq(currency),
                eq(transactionDate.toLocalDate()),
                eq(transactionDate.minusMonths(6).toLocalDate()))
        ).thenReturn(Optional.of(new ExchangeRate(currency, exchangeRate, transactionDate.toLocalDate())));

        PurchaseConversion result = subject.convertPurchase(purchaseId, currency);

        assertNotNull(result);
        assertEquals(purchaseId, result.id());
        assertEquals(purchaseModel.getDescription(), result.description());
        assertEquals(purchaseModel.getTransactionDate(), result.transactionDate());
        assertEquals(expectedConvertedAmount, result.amount());
        assertEquals(exchangeRate, result.rate());
        assertEquals(purchaseModel.getAmount(), result.originalAmount());
    }

    @Test
    void shouldRoundAmountForConvertedPurchase() {
        String currency = "EUR";
        BigDecimal exchangeRate = new BigDecimal("0.85555");
        BigDecimal expectedConvertedAmount = purchaseModel.getAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchaseModel));
        when(treasuryAPI.getLatestExchangeRate(
                eq(currency),
                eq(transactionDate.toLocalDate()),
                eq(transactionDate.minusMonths(6).toLocalDate()))
        ).thenReturn(Optional.of(new ExchangeRate(currency, exchangeRate, transactionDate.toLocalDate())));

        PurchaseConversion result = subject.convertPurchase(purchaseId, currency);

        assertNotNull(result);
        assertEquals(purchaseId, result.id());
        assertEquals(purchaseModel.getDescription(), result.description());
        assertEquals(purchaseModel.getTransactionDate(), result.transactionDate());
        assertEquals(expectedConvertedAmount, result.amount());
        assertEquals(exchangeRate, result.rate());
        assertEquals(purchaseModel.getAmount(), result.originalAmount());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenPurchaseNotFound() {
        String currency = "EUR";
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subject.convertPurchase(purchaseId, currency)
        );

        assertEquals("Purchase not found with id: " + purchaseId, exception.getMessage());
        verify(purchaseRepository).findById(purchaseId);
        verifyNoInteractions(treasuryAPI);
    }

    @Test
    void shouldThrowCurrencyConversionExceptionWhenNoExchangeRate() {
        String currency = "EUR";
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchaseModel));
        when(treasuryAPI.getLatestExchangeRate(any(), any(), any())).thenReturn(Optional.empty());

        CurrencyConversionException exception = assertThrows(
                CurrencyConversionException.class,
                () -> subject.convertPurchase(purchaseId, currency)
        );

        assertTrue(exception.getMessage().contains("No valid exchange rate found for " + currency));
        verify(purchaseRepository).findById(purchaseId);
        verify(treasuryAPI).getLatestExchangeRate(
                eq(currency),
                eq(transactionDate.toLocalDate()),
                eq(transactionDate.minusMonths(6).toLocalDate())
        );
    }
}