package com.leander.cinema.dto.CustomerDto.ticketDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TicketRequestDto(
        @Min(value = 1, message = "Antal biljetter måste vara minst 1")
        @Max(value = 20, message = "Max antal biljetter är 20")
        int numberOfTickets,
        @NotNull(message = "Föreställning måste anges")
        Long screeningId
) {
}
