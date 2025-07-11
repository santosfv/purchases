package com.santosfv.purchases.treasury;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(String unit, BigDecimal rate, LocalDate effectiveDate) {
}