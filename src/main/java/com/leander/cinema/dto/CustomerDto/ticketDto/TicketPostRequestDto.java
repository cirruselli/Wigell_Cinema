package com.leander.cinema.dto.CustomerDto.ticketDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TicketPostRequestDto(
        @NotNull(message = "Föreställning måste anges")
        Long screeningId,
        @Min(value = 1, message = "Antal biljetter måste vara minst 1")
        @Max(value = 20, message = "Max antal biljetter är 20")
        int numberOfTickets
) {
}
