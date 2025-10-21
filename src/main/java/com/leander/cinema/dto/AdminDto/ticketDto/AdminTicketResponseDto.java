package com.leander.cinema.dto.AdminDto.ticketDto;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;

import java.math.BigDecimal;

public record AdminTicketResponseDto(
        Long ticketId,
        int numberOfTickets,
        String customerFirstName,
        String customerLastName,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        AdminScreeningResponseDto screening,
        AdminBookingResponseDto booking
) {
}
