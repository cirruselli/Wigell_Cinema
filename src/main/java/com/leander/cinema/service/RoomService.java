package com.leander.cinema.service;

import com.leander.cinema.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


}
