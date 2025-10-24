package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingSpeakerResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        String roomName,
        List<String> roomEquipment,
        int maxGuests,
        String speakerName,
        String firstName,
        String lastName)
implements BookingResponseContent {
}