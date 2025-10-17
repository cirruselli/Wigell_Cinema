package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.entity.Screening;

import java.math.BigDecimal;
import java.util.List;

public class ScreeningMapper {
    public static Screening toScreeningEntity(AdminScreeningRequestDto body) {
        return new Screening(
                body.startTime(),
                body.endTime(),
                body.priceSek()
        );
    }

    public static AdminScreeningResponseDto toAdminScreeningResponseDto(Screening screening) {

        // Mappa room till DTO
        AdminRoomResponseDto roomDto = null;
        if (screening.getRoom() != null) {
            var room = screening.getRoom();
            roomDto = new AdminRoomResponseDto(
                    room.getId(),
                    room.getName(),
                    room.getMaxGuests(),
                    room.getPriceSek(),
                    room.getPriceUsd(),
                    room.getStandardEquipment()
            );
        }

        // Mappa movie till DTO
        AdminMovieResponseDto movieDto = null;
        if (screening.getMovie() != null) {
            var movie = screening.getMovie();
            movieDto = new AdminMovieResponseDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getAgeLimit(),
                    movie.getDuration()
            );
        }

        // Returnera screening DTO
        return new AdminScreeningResponseDto(
                screening.getId(),
                screening.getStartTime(),
                screening.getEndTime(),
                screening.getPriceSek(),
                screening.getPriceUsd(),
                roomDto,
                movieDto
        );
    }
}
