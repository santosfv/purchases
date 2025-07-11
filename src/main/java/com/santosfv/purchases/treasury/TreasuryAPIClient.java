package com.santosfv.purchases.treasury;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.santosfv.purchases.CurrencyConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

class TreasuryAPIClient implements TreasuryAPI {

    private static final Logger LOG = LoggerFactory.getLogger(TreasuryAPIClient.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String ratesEndpoint;

    TreasuryAPIClient(RestTemplate restTemplate, String apiUrl, String ratesEndpoint) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.ratesEndpoint = ratesEndpoint;
    }

    @Override
    public Optional<ExchangeRate> getLatestExchangeRate(String currency, LocalDate endDate, LocalDate startDate) throws CurrencyConversionException {
        String url = UriComponentsBuilder.fromUriString(apiUrl + ratesEndpoint)
                .queryParam("fields", "country_currency_desc,exchange_rate,record_date")
                .queryParam("filter", "country_currency_desc:eq:" + currency + ",record_date:gte:" + startDate + ",record_date:lte:" + endDate)
                .queryParam("sort", "-record_date")
                .queryParam("page[size]", "1")
                .queryParam("format", "json")
                .toUriString();

        LOG.debug("Fetching exchange rates from URL: {}", url);
        TreasuryAPIResponse rates = restTemplate.getForObject(url, TreasuryAPIResponse.class);
        LOG.debug("Retrieved exchange rates for {}: {}", currency, rates);

        if (rates == null || rates.data() == null || rates.data().isEmpty()) {
            return Optional.empty();
        }

        return rates.data.stream()
                .max(Comparator.comparing(Currency::date))
                .map(currencyRate -> new ExchangeRate(currency, currencyRate.rate(), currencyRate.date()));
    }

    record Currency(
            @JsonProperty("country_currency_desc")
            String currency,

            @JsonProperty("exchange_rate")
            BigDecimal rate,

            @JsonProperty("record_date")
            LocalDate date
    ) {
    }

    record TreasuryAPIResponse(
            @JsonProperty("data")
            List<Currency> data
    ) {
    }
}
