package com.leander.cinema.dto.AdminDto.ticketDto;

import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;

import java.math.BigDecimal;

public record AdminTicketScreeningResponseDto(
        Long ticketId,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        AdminScreeningResponseDto screening
) implements AdminTicketResponseContent {
}


