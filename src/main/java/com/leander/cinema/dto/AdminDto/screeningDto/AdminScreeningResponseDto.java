package com.leander.cinema.dto.AdminDto.screeningDto;

import java.time.LocalDateTime;

public record AdminScreeningResponseDto(
        Long id,
        LocalDateTime startTime,
        String name,
        String title
) {
}
