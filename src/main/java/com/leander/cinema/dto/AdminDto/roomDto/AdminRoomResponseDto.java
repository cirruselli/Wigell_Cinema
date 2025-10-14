package com.leander.cinema.dto.AdminDto.roomDto;

import java.math.BigDecimal;
import java.util.List;

public record AdminRoomResponseDto(
        Long id,
        String name,
        int maxGuests,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        List<String> standardEquipment
) {
}
