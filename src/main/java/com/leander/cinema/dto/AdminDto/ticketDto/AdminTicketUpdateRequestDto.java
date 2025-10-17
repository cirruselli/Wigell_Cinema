package com.leander.cinema.dto.AdminDto.ticketDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdminTicketUpdateRequestDto(
        @NotNull(message = "Biljett-ID måste anges")
        Long ticketId,
        @Min(value = 1, message = "Antal biljetter måste vara minst 1")
        @Max(value = 6, message = "Max antal biljetter är 6")
        int numberOfTickets,
        Long screeningId,
        Long bookingId){
}