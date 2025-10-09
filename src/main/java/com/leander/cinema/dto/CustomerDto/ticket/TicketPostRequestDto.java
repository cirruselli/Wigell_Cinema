package com.leander.cinema.dto.CustomerDto.ticket;

import jakarta.validation.constraints.NotNull;

public record TicketPostRequestDto(
        @NotNull(message = "Föreställning måste anges")
        Long screeningId
) {
}
