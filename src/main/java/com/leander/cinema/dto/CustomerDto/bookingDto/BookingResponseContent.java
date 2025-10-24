package com.leander.cinema.dto.CustomerDto.bookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingResponseContent {
    Long bookingId();
    LocalDateTime reservationStartTime();
    LocalDateTime reservationEndTime();
    int numberOfGuests();
    BigDecimal totalPriceSek();
    BigDecimal totalPriceUsd();
    List<String> roomEquipment();
    String roomName();
    int maxGuests();
    String firstName();
    String lastName();
}