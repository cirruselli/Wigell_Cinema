package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomRequestDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.entity.Room;
import com.leander.cinema.exception.RoomAlreadyExistsException;
import com.leander.cinema.mapper.RoomMapper;
import com.leander.cinema.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminRoomResponseDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<AdminRoomResponseDto> responseList = new ArrayList<>();
        for (Room room : rooms) {
            AdminRoomResponseDto roomResponse = RoomMapper.toAdminRoomResponseDto(room);
            responseList.add(roomResponse);
        }
        return responseList;
    }

    @Transactional(readOnly = true)
    public AdminRoomResponseDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Rum med id " + id + " hittades inte"));
        return RoomMapper.toAdminRoomResponseDto(room);
    }

    @Transactional
    public AdminRoomResponseDto createRoom(AdminRoomRequestDto body) {

        if (roomRepository.existsByName(body.name())) {
            throw new RoomAlreadyExistsException("Rum med detta namn finns redan");
        }

        Room room = RoomMapper.toRoomEntity(body);

        BigDecimal priceSek = body.priceSek();
        BigDecimal factor = new BigDecimal("0.11");
        BigDecimal priceUsd = priceSek.multiply(factor);

        room.setPriceUsd(priceUsd);

        if(body.standardEquipment() == null){
            room.setStandardEquipment(new ArrayList<>(Arrays.asList("Mikrofon", "Högtalare", "Projektor")));
        }

        roomRepository.save(room);
        return RoomMapper.toAdminRoomResponseDto(room);
    }
}
