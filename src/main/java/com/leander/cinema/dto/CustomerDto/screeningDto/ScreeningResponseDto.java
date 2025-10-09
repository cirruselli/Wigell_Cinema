package com.leander.cinema.dto.CustomerDto.screeningDto;

import java.time.LocalDateTime;

public record ScreeningResponseDto(
        LocalDateTime startTime,
        String roomName,
        String title
) {
}
