package com.leander.cinema.dto.AdminDto.screeningDto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminScreeningRequestDto(
        @NotNull(message = "Starttid måste anges")
        LocalDateTime startTime,
        @NotNull(message = "Pris i SEK måste anges")
        @DecimalMin(value = "0.01", message = "Pris måste vara minst 0.01")
        BigDecimal priceSek,
        @NotNull(message = "Rum måste anges")
        Long roomId,
        @NotNull(message = "Film måste anges")
        Long movieId
) {
}
