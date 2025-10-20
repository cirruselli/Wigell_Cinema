package com.leander.cinema.dto.CustomerDto.ticketDto;

import java.time.LocalDateTime;

public record TicketBookingResponseDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String roomName,
        String speakerName
) {
}
