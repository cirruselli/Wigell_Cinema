package com.leander.cinema.dto.CustomerDto.ticketDto;

import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketBookingResponseContentDto(
        Long ticketId,
        String firstName,
        String lastName,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String roomName,
        String speakerName,
        MovieResponseDto movie
) implements TicketResponseContent {
}
