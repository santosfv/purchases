package com.santosfv.purchases.treasury;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryAPIClientTest {

    private static final String API_URL = "https://api.example.com";
    private static final String RATES_ENDPOINT = "/rates";

    @Mock
    private RestTemplate restTemplate;

    private TreasuryAPIClient subject;

    @BeforeEach
    void setUp() {
        subject = new TreasuryAPIClient(restTemplate, API_URL, RATES_ENDPOINT);
    }

    @Test
    void shouldBuildURIWithEndpoint() {
        String currency = "EUR";
        LocalDate date = LocalDate.now();
        TreasuryAPIClient.TreasuryAPIResponse mockResponse = new TreasuryAPIClient.TreasuryAPIResponse(
                List.of(new TreasuryAPIClient.Currency(currency, new BigDecimal("0.85"), date.minusDays(1)))
        );

        when(restTemplate.getForObject(anyString(), eq(TreasuryAPIClient.TreasuryAPIResponse.class)))
                .thenReturn(mockResponse);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        subject.getLatestExchangeRate(currency, date, date.minusMonths(6));

        verify(restTemplate).getForObject(urlCaptor.capture(), eq(TreasuryAPIClient.TreasuryAPIResponse.class));
        assertTrue(urlCaptor.getValue().contains("country_currency_desc:eq:EUR"));
    }

    @Test
    void shouldGetLatestExchangeRate() {
        String currency = "EUR";
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();
        TreasuryAPIClient.TreasuryAPIResponse mockResponse = new TreasuryAPIClient.TreasuryAPIResponse(
                List.of(new TreasuryAPIClient.Currency(currency, new BigDecimal("0.85"), endDate.minusDays(1)))
        );
        when(restTemplate.getForObject(anyString(), eq(TreasuryAPIClient.TreasuryAPIResponse.class)))
                .thenReturn(mockResponse);

        Optional<ExchangeRate> result = subject.getLatestExchangeRate(currency, endDate, startDate);

        assertTrue(result.isPresent());
        assertEquals(currency, result.get().currency());
        assertEquals(new BigDecimal("0.85"), result.get().rate());
        assertEquals(endDate.minusDays(1), result.get().effectiveDate());
    }

    @Test
    void shouldGetLatestExchangeRateOrderedByDate() {
        String currency = "EUR";
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();
        TreasuryAPIClient.TreasuryAPIResponse mockResponse = new TreasuryAPIClient.TreasuryAPIResponse(
                List.of(
                        new TreasuryAPIClient.Currency(currency, new BigDecimal("0.55"), endDate.minusDays(1)),
                        new TreasuryAPIClient.Currency(currency, new BigDecimal("0.95"), endDate.minusDays(5))
                )
        );
        when(restTemplate.getForObject(anyString(), eq(TreasuryAPIClient.TreasuryAPIResponse.class)))
                .thenReturn(mockResponse);

        Optional<ExchangeRate> result = subject.getLatestExchangeRate(currency, endDate, startDate);

        assertTrue(result.isPresent());
        assertEquals(currency, result.get().currency());
        assertEquals(new BigDecimal("0.55"), result.get().rate());
        assertEquals(endDate.minusDays(1), result.get().effectiveDate());
    }

    @Test
    void shouldReturnEmptyForGetLatestExchangeRateWHenEmptyDataList() {
        String currency = "EUR";
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();

        when(restTemplate.getForObject(anyString(), eq(TreasuryAPIClient.TreasuryAPIResponse.class)))
                .thenReturn(new TreasuryAPIClient.TreasuryAPIResponse(Collections.emptyList()));

        Optional<ExchangeRate> result = subject.getLatestExchangeRate(currency, endDate, startDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyForGetLatestExchangeRateWHenNullResponse() {
        String currency = "EUR";
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();

        when(restTemplate.getForObject(anyString(), eq(TreasuryAPIClient.TreasuryAPIResponse.class)))
                .thenReturn(null);

        Optional<ExchangeRate> result = subject.getLatestExchangeRate(currency, endDate, startDate);

        assertTrue(result.isEmpty());
    }
}