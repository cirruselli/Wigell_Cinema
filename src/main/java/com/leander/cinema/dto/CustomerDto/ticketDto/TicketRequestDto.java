package com.leander.cinema.dto.CustomerDto.ticketDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TicketRequestDto(
        @Min(value = 1, message = "Antal biljetter måste vara minst 1")
        @Max(value = 6, message = "Max antal biljetter är 6")
        int numberOfTickets,
        Long screeningId,
        Long bookingId
) {
}
