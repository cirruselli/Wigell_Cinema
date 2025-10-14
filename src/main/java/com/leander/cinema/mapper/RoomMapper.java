package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomRequestDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.entity.Room;

public class RoomMapper {
    public static AdminRoomResponseDto toAdminRoomResponseDto (Room room) {
        return new AdminRoomResponseDto(
                room.getId(),
                room.getName(),
                room.getMaxGuests(),
                room.getPriceSek(),
                room.getPriceUsd(),
                room.getStandardEquipment()
        );
    }

    public static Room toRoomEntity (AdminRoomRequestDto requestDto) {
        return new Room(
                requestDto.name().trim(),
                requestDto.maxGuests(),
                requestDto.priceSek(),
                requestDto.standardEquipment()
        );
    }
}
