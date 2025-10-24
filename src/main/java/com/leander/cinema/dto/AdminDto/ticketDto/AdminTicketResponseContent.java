package com.leander.cinema.dto.AdminDto.ticketDto;

import java.math.BigDecimal;

public interface AdminTicketResponseContent {
    Long ticketId();
    int numberOfTickets();
    BigDecimal priceSek();
    BigDecimal priceUsd();
    BigDecimal totalPriceSek();
    BigDecimal totalPriceUsd();

}
