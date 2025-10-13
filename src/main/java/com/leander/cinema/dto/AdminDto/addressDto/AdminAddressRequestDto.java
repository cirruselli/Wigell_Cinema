package com.leander.cinema.dto.AdminDto.addressDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminAddressRequestDto(
        @NotBlank(message = "Gatuadress måste anges")
        @Size(max = 100, message = "Teckenlängd max 100")
        String street,
        @NotBlank(message = "Postnummer måste anges")
        @Size(min = 5, max = 5, message = "Teckenlängd måste vara 5")
        String postalCode,
        @NotBlank(message = "Stad måste anges")
        @Size(max = 100, message = "Teckenlängd max 100")
        String city
) {
}
