package com.leander.cinema.dto.AdminDto.ticketDto;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;

import java.math.BigDecimal;

public record AdminTicketResponseDto(
        Long id,
        int numberOfTickets,
        String customerFistName,
        String customerLastName,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        AdminScreeningResponseDto screeningDto,
        AdminBookingResponseDto bookingDto
) {
}
