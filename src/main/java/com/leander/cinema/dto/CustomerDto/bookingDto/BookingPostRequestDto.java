package com.leander.cinema.dto.CustomerDto.bookingDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record BookingPostRequestDto(
        @NotNull(message = "Datum och tid måste anges")
        @FutureOrPresent(message = "Startdatum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationStartTime,
        @NotNull(message = "Datum och tid måste anges")
        @FutureOrPresent(message = "Slutdatum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationEndTime,
        @NotNull(message = "Antal gäster måste anges")
        @Min(value = 1, message = "Antal gäster måste vara minst 1")
        Integer numberOfGuests,
        @NotNull(message = "Utrustning måste anges")
        List<String> equipment,
        @NotNull(message = "Rum måste anges")
        Long roomId,
        @NotNull(message = "Föreställning måste anges")
        Long screeningId
) {
}
