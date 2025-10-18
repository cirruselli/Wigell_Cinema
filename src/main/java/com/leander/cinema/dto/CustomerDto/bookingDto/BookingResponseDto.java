package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        String roomName,
        List<String> equipment,
        int maxGuests,
        String speakerName,
        String movieTitle,
        String genre,
        int ageLimit,
        int duration,
        String customerFirstName,
        String customerLastName,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd
) {
}
