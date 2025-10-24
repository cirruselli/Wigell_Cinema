package com.leander.cinema.currencyConverter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CurrencyConverter {
    private final BigDecimal USD = new BigDecimal("0.11");

    public BigDecimal toUSD(BigDecimal sek) {
        validateAmount(sek);
        return USD.multiply(sek);
    }

    // Hjälpmetod för validering
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Belopp kan inte vara null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Belopp kan inte vara negativt");
        }
    }
}
