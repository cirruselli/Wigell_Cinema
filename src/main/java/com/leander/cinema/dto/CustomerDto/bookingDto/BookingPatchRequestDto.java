package com.leander.cinema.dto.CustomerDto.bookingDto;

import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

public record BookingPatchRequestDto(
        @FutureOrPresent(message = "Datum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationStartTime,
        @FutureOrPresent(message = "Datum/tid kan inte vara bakåt i tiden")
        LocalDateTime reservationEndTime,
        List<String> roomEquipment
) {
}
