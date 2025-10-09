package com.leander.cinema.dto.CustomerDto.booking;

import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

public record BookingPatchRequestDto(
        @FutureOrPresent(message = "Datum kan inte vara bakåt i tiden")
        LocalDateTime reservationTime,
        List<String> equipment
) {
}
