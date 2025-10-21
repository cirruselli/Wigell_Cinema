package com.leander.cinema.dto.AdminDto.screeningDto;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public record AdminScreeningResponseDto(
        Long screeningId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime totalEndTime,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        AdminRoomResponseDto room,
        AdminMovieResponseDto movie){
}
