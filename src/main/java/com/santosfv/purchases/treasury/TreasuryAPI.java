package com.santosfv.purchases.treasury;

import com.santosfv.purchases.CurrencyConversionException;

import java.time.LocalDate;
import java.util.Optional;

public interface TreasuryAPI {

    /**
     * Retrieves the latest exchange rate for a given currency on a specific date interval.
     *
     * @param currency     The currency code (e.g., "USD", "EUR").
     * @param endDate The final date to retrieve the exchange rate for.
     * @param startDate The initial date to retrieve the exchange rate for.
     * @return An Optional containing the latest ExchangeRate if found, or empty if not.
     */
    Optional<ExchangeRate> getLatestExchangeRate(String currency, LocalDate endDate, LocalDate startDate) throws CurrencyConversionException;
}
