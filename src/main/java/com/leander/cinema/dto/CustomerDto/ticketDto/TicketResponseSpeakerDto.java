package com.leander.cinema.dto.CustomerDto.ticketDto;

import java.math.BigDecimal;

public record TicketResponseSpeakerDto(
        String firstname,
        String lastname,
        String speakerName,
        int numberOfTickets,
        String roomName,
        BigDecimal priceSek,
        BigDecimal priceUsd
) {
}
