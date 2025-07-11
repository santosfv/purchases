package com.santosfv.purchases.treasury;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TreasuryAPIConfiguration {

    @Bean
    @ConditionalOnMissingClass
    TreasuryAPI treasuryAPI(
            @Value("${treasury.api.url}") String apiUrl,
            @Value("${treasury.api.ratesEndpoint}") String ratesEndpoint) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new TreasuryAPIClient(builder.build(), apiUrl, ratesEndpoint);
    }
}
