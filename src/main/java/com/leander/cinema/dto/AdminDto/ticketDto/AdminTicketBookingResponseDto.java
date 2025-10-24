package com.leander.cinema.dto.AdminDto.ticketDto;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseContent;
import java.math.BigDecimal;

public record AdminTicketBookingResponseDto(
        Long ticketId,
        int numberOfTickets,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        AdminBookingResponseContent booking
) implements AdminTicketResponseContent { }

