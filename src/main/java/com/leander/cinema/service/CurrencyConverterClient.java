package com.leander.cinema.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterClient {

    private final WebClient webClient;

    public CurrencyConverterClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8851/api/currency").build();
    }

    public BigDecimal convertSekToUsd(BigDecimal sek) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/usd").queryParam("sek", sek).build())
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .block(); // block() = vänta på resultatet
    }
}
