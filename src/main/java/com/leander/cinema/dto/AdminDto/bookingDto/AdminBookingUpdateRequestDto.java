package com.leander.cinema.dto.AdminDto.bookingDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AdminBookingUpdateRequestDto(
        @NotNull(message = "Boknings-ID måste anges")
        Long bookingId,
        @NotNull(message = "Datum och tid måste anges")
        @FutureOrPresent(message = "Startdatum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationStartTime,
        @NotNull(message = "Datum och tid måste anges")
        @FutureOrPresent(message = "Slutdatum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationEndTime,
        @NotNull
        @Min(value = 1, message = "Antal gäster måste vara minst 1")
        int numberOfGuests,
        String speakerName,
        Long screeningId,
        @NotNull(message = "Rum måste anges på bokningen")
        Long roomId)
    {
}
