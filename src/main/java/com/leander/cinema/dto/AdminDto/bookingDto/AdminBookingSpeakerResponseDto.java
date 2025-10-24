package com.leander.cinema.dto.AdminDto.bookingDto;

import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.service.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminBookingSpeakerResponseDto(
        Long bookingId,
        LocalDateTime reservationStartTime,
        LocalDateTime reservationEndTime,
        int numberOfGuests,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd,
        List<String> bookingEquipment,
        BookingStatus bookingStatus,
        AdminRoomResponseDto room,
        String speakerName
) implements AdminBookingResponseContent {
}