package com.leander.cinema.dto.AdminDto.roomDto;

import java.util.List;

public record AdminRoomResponseDto(
        Long id,
        String name,
        int maxGuests,
        List<String> standardEquipment
) {
}
