package com.leander.cinema.dto.AdminDto.screeningDto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AdminScreeningRequestDto(
        @NotNull(message = "Starttid måste anges")
        LocalDateTime startTime,
        String speakerName,
        String movieTitle
) {
}
