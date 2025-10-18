package com.leander.cinema.dto.AdminDto.bookingDto;

import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.service.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public record AdminBookingResponseDto(
        Long bookingId,
        BookingStatus status,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        AdminRoomResponseDto room,
        String speakerName,
        AdminScreeningResponseDto screening) {
}
