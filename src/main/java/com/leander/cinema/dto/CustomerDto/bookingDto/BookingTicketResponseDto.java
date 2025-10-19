package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.time.LocalDateTime;

public record BookingTicketResponseDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String roomName,
        String speakerName
) {
}
