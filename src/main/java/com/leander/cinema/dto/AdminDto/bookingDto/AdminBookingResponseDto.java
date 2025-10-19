package com.leander.cinema.dto.AdminDto.bookingDto;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.service.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminBookingResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        List<String> roomEquipment,
        AdminRoomResponseDto room,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        String speakerName,
        AdminMovieResponseDto movie,
        BookingStatus bookingStatus) {
}
