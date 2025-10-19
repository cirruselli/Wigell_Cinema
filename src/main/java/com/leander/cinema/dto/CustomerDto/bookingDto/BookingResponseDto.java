package com.leander.cinema.dto.CustomerDto.bookingDto;

import com.leander.cinema.service.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDto(
        Long bookingId,
        BookingStatus bookingStatus,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        String roomName,
        List<String> roomEquipment,
        int maxGuests,
        String speakerName,
        String movieTitle,
        String genre,
        int ageLimit,
        int duration,
        String customerFirstName,
        String customerLastName
) {
}
