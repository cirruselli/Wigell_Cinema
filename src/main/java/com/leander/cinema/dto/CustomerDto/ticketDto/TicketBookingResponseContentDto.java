package com.leander.cinema.dto.CustomerDto.ticketDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Gör att endast fält med ej null visas = antingen visas movie eller speakerName i svaret!
@JsonInclude(JsonInclude.Include.NON_NULL)
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
