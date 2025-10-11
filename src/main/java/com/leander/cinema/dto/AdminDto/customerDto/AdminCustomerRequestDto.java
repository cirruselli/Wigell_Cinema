package com.leander.cinema.dto.AdminDto.customerDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCustomerRequestDto(
        @NotBlank(message = "Förnamn måste anges")
        @Size(max = 50, message = "Teckenlängd max 50")
        String firstName,
        @NotBlank(message = "Efternamn måste anges")
        @Size(max = 50, message = "Teckenlängd max 50")
        String lastName,
        @Email(message = "Ogiltig e-postadress")
        @NotBlank(message = "Email måste anges")
        @Size(max = 255, message = "Teckenlängd max 255")
        String email,
        @NotBlank(message = "Telefonnummer måste anges")
        @Size(max = 10, message = "Teckenlängd max 10")
        String phone
) {
}
