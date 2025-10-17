package com.leander.cinema.dto.CustomerDto.ticketDto;

import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;

import java.math.BigDecimal;

public record TicketResponseDto(
        String customerFirstName,
        String customerLastName,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        MovieResponseDto movieDto,
        String speakerName,
        String roomName
) {
}
