package com.leander.cinema.dto.CustomerDto.bookingDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingPostRequestDto(
        @NotNull(message = "Datum måste anges")
        @FutureOrPresent(message = "Datum kan inte vara bakåt i tiden")
        LocalDateTime reservationTime,
        @Min(value = 1, message = "Antal gäster måste vara minst 1")
        int numberOfGuests,
        @NotNull(message = "Rum måste anges")
        Long roomId,
        @NotNull(message = "Föreställning måste anges")
        Long screeningId
) {
}
