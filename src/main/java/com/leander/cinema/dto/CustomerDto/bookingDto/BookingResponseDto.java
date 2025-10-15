package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        String roomName,
        int maxGuests,
        String speakerName,
        String movieTitle,
        String customerFirstName,
        String customerLastName,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd
) {
}
