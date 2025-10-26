package com.leander.cinema.service;

import com.leander.cinema.currency.CurrencyConverter;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomRequestDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.entity.Room;
import com.leander.cinema.exception.RoomAlreadyExistsException;
import com.leander.cinema.mapper.RoomMapper;
import com.leander.cinema.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoomService {

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final CurrencyConverter currencyConverter;

    public RoomService(RoomRepository roomRepository,
                       CurrencyConverter currencyConverter) {
        this.roomRepository = roomRepository;
        this.currencyConverter = currencyConverter;
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
                .orElseThrow(() -> new EntityNotFoundException("Rum med id " + id + " hittades inte"));
        return RoomMapper.toAdminRoomResponseDto(room);
    }

    @Transactional
    public AdminRoomResponseDto createRoom(AdminRoomRequestDto body) {

        if (roomRepository.existsByName(body.name())) {
            throw new RoomAlreadyExistsException("Rum med detta namn finns redan");
        }

        Room room = RoomMapper.toRoomEntity(body);

        BigDecimal priceSek = body.priceSek();
        BigDecimal priceUsd = currencyConverter.toUsd(priceSek);

        room.setPriceUsd(priceUsd);

        if (body.standardEquipment() == null) {
            room.setStandardEquipment(new ArrayList<>(Arrays.asList("Mikrofon", "Högtalare", "Projektor")));
        }

        roomRepository.save(room);

        logger.info("Admin skapade lokal {}", room.getId());

        return RoomMapper.toAdminRoomResponseDto(room);
    }

    @Transactional
    public AdminRoomResponseDto updateRoom(Long id, AdminRoomRequestDto body) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rum med id " + id + " hittades inte"));

        RoomMapper.updateRoom(room, body);

        BigDecimal priceSek = body.priceSek();
        BigDecimal priceUsd = currencyConverter.toUsd(priceSek);
        room.setPriceUsd(priceUsd);

        if (body.standardEquipment() == null) {
            room.setStandardEquipment(new ArrayList<>(Arrays.asList("Mikrofon", "Högtalare", "Projektor")));
        }

        roomRepository.save(room);

        logger.info("Admin uppdaterade lokal {}", room.getId());

        return RoomMapper.toAdminRoomResponseDto(room);
    }
}
