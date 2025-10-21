package com.leander.cinema.dto.CustomerDto.ticketDto;

import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;

import java.math.BigDecimal;

public record TicketResponseDto(
        Long ticketId,
        String customerFirstName,
        String customerLastName,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        ScreeningResponseDto screening,
        TicketBookingResponseDto booking
) {
}
