package com.leander.cinema.dto.AdminDto.roomDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AdminRoomRequestDto(
        @NotBlank(message = "Lokalens namn måste anges")
        @Size(max = 50, message = "Teckenlängd max 50")
        String name,
        @NotNull(message = "Max antal gäster måste anges")
        int maxGuests,
        @NotNull(message = "Standardutrustning måste anges")
        List<String> standardEquipment
) {
}
