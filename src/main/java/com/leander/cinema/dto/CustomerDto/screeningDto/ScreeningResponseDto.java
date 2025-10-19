package com.leander.cinema.dto.CustomerDto.screeningDto;

import java.time.LocalDateTime;

public record ScreeningResponseDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String movieTitle,
        String genre,
        int ageLimit,
        int duration,
        String roomName
) {
}
