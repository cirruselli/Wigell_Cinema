package com.leander.cinema.dto.CustomerDto.bookingDto;


import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingMovieResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        List<String> roomEquipment,
        String roomName,
        int maxGuests,
        String firstName,
        String lastName,
        MovieResponseDto movie)
        implements BookingResponseContent {
}