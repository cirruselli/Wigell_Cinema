package com.leander.cinema.dto.CustomerDto.ticketDto;

import java.math.BigDecimal;

public interface TicketResponseContent {
    Long ticketId();
    String firstName();
    String lastName();
    int numberOfTickets();
    BigDecimal priceSek();
    BigDecimal priceUsd();
    BigDecimal totalPriceSek();
    BigDecimal totalPriceUsd();
}
