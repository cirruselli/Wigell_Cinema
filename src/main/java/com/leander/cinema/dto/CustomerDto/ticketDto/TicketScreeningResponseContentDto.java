package com.leander.cinema.dto.CustomerDto.ticketDto;

import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;

import java.math.BigDecimal;

public record TicketScreeningResponseContentDto(
        Long ticketId,
        String firstName,
        String lastName,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        ScreeningResponseDto screening
) implements TicketResponseContent {
}
