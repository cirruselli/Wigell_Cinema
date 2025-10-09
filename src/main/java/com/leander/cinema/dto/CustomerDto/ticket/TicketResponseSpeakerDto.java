package com.leander.cinema.dto.CustomerDto.ticket;

import java.math.BigDecimal;

public record TicketResponseSpeakerDto(
        String firstname,
        String lastname,
        String speakerName,
        String roomName,
        BigDecimal priceSek,
        BigDecimal priceUsd
) {
}
