package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.entity.Ticket;


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

    public static ScreeningResponseDto toScreeningResponseDto(Ticket ticket) {
        Screening screening = ticket.getScreening();
        String movieTitle = "----";
        String genre = "----";
        int ageLimit = 0;
        int duration = 0;

        if (ticket.getScreening() != null) {

            if (screening.getMovie() != null) {
                if (screening.getMovie().getTitle() != null) movieTitle = screening.getMovie().getTitle();
                if (screening.getMovie().getGenre() != null) genre = screening.getMovie().getGenre();
                ageLimit = screening.getMovie().getAgeLimit();
                duration = screening.getMovie().getDuration();
            }
        }

        return new ScreeningResponseDto(
                screening.getStartTime(),
                screening.getEndTime(),
                movieTitle,
                genre,
                ageLimit,
                duration
        );
    }
}
