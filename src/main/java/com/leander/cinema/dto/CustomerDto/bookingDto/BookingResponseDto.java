package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDto(
        Long id,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        List<String> equipments,//om bokningslistan är null (att inget värde finns innan en patch gjorts) så fylls den med listan från room!
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
