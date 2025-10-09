package com.leander.cinema.dto.CustomerDto.ticket;

import java.math.BigDecimal;

public record TicketResponseMovieDto(
        String firstname,
        String lastname,
        String movieName,
        double duration,
        String roomName,
        BigDecimal priceSek,
        BigDecimal priceUsd
) {
}
