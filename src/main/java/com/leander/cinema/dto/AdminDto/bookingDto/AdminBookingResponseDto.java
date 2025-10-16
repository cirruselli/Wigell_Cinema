package com.leander.cinema.dto.AdminDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminBookingResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        Long roomId,
        String roomName,
        int maxGuests,
        List<String> equipments,
        String speakerName,
        Long movieId,
        String movieTitle,
        int movieDuration,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd
) {
}
