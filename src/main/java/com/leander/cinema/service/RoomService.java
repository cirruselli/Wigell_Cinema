package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.entity.Room;
import com.leander.cinema.mapper.RoomMapper;
import com.leander.cinema.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {
    private RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<AdminRoomResponseDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<AdminRoomResponseDto> responseList = new ArrayList<>();
        for (Room room : rooms) {
            AdminRoomResponseDto roomResponse = RoomMapper.toAdminRoomResponseDto(room);
            responseList.add(roomResponse);
        }
        return responseList;
    }

    public AdminRoomResponseDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Rum med id " + id + " hittades inte"));
        return RoomMapper.toAdminRoomResponseDto(room);
    }
}
