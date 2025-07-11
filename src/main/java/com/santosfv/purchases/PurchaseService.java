package com.santosfv.purchases;

import com.santosfv.purchases.controllers.PurchaseConversion;
import com.santosfv.purchases.controllers.PurchaseRequest;
import com.santosfv.purchases.repository.PurchaseModel;
import com.santosfv.purchases.repository.PurchaseRepository;
import com.santosfv.purchases.treasury.ExchangeRate;
import com.santosfv.purchases.treasury.TreasuryAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TreasuryAPI treasuryAPI;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, TreasuryAPI treasuryAPI) {
        this.purchaseRepository = purchaseRepository;
        this.treasuryAPI = treasuryAPI;
    }

    @Transactional
    public PurchaseModel createPurchase(PurchaseRequest purchaseRequest) {
        PurchaseModel model = PurchaseModel.from(purchaseRequest);
        return purchaseRepository.save(model);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseModel> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id);
    }

    public PurchaseConversion convertPurchase(UUID id, String currency) {
        PurchaseModel purchase = getPurchaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        LocalDateTime purchaseDate = purchase.getTransactionDate();
        ExchangeRate exchangeRate = treasuryAPI.getLatestExchangeRate(
                        currency,
                        purchaseDate.toLocalDate(),
                        purchaseDate.minusMonths(6).toLocalDate())
                .orElseThrow(() -> new CurrencyConversionException(
                        "No valid exchange rate found for " + currency + " on or six months before " + purchaseDate.toLocalDate()));

        BigDecimal convertedAmount = purchase.getAmount()
                .multiply(exchangeRate.rate())
                .setScale(2, RoundingMode.HALF_UP);

        return new PurchaseConversion(
                purchase.getId(),
                purchase.getDescription(),
                purchase.getTransactionDate(),
                convertedAmount,
                exchangeRate.rate(),
                purchase.getAmount()
        );
    }
}