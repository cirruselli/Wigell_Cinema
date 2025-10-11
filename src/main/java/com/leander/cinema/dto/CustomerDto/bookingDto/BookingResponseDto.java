package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDto(
        LocalDateTime reservationTime,
        int numberOfGuests,
        List<String> equipments,//om bokningslistan är null (att inget värde finns innan en patch gjorts) så fylls den med listan från room!
        String roomName,
        int maxGuests,
        String speakerName,
        String movieTitle,
        String customerName,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd
) {
}
