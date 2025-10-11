package com.leander.cinema.dto.CustomerDto.ticketDto;

import java.math.BigDecimal;

public record TicketResponseMovieDto(
        String firstname,
        String lastname,
        String movieName,
        double duration,
        int numberOfTickets,
        String roomName,
        BigDecimal priceSek,
        BigDecimal priceUsd
) {
}
