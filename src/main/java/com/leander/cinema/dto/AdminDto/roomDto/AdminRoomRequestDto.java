package com.leander.cinema.dto.AdminDto.roomDto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record AdminRoomRequestDto(
        @NotBlank(message = "Lokalens namn måste anges")
        @Size(max = 50, message = "Teckenlängd max 50")
        String name,
        @NotNull(message = "Max antal gäster måste anges")
        @Min(value = 1)
        Integer maxGuests,
        @NotNull(message = "Pris i SEK måste anges")
        @DecimalMin(value = "0.01", message = "Pris måste vara minst 0.01")
        BigDecimal priceSek,
        List<String> standardEquipment
) {
}
