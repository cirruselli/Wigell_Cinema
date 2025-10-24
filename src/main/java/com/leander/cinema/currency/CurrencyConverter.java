package com.leander.cinema.currency;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CurrencyConverter {
    private final BigDecimal usd = new BigDecimal("0.11");

    public BigDecimal toUsd(BigDecimal sek) {
        return usd.multiply(sek);
    }
}
