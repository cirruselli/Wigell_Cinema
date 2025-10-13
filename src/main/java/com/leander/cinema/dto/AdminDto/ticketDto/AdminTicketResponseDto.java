package com.leander.cinema.dto.AdminDto.ticketDto;

import java.math.BigDecimal;

public record AdminTicketResponseDto(
        Long id,
        int numberOfTickets,
        String fistName,
        String lastName,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        Long screeningId
) {
}
