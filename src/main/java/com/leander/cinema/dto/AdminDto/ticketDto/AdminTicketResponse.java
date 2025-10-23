package com.leander.cinema.dto.AdminDto.ticketDto;

import java.math.BigDecimal;

public interface AdminTicketResponse {
    Long ticketId();
    int numberOfTickets();
    BigDecimal priceSek();
    BigDecimal priceUsd();
    BigDecimal totalPriceSek();
    BigDecimal totalPriceUsd();

}
