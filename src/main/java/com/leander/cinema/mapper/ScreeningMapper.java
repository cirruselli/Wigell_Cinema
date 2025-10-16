package com.leander.cinema.mapper;

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

        // === Room ===
        Long roomId = null;
        String roomName = "";
        int maxGuests = 0;
        BigDecimal roomPriceSek = null;
        BigDecimal roomPriceUsd = null;
        List<String> roomEquipment = List.of();

        if (screening.getRoom() != null) {
            roomId = screening.getRoom().getId();
            if (screening.getRoom().getName() != null)
                roomName = screening.getRoom().getName();
            maxGuests = screening.getRoom().getMaxGuests();
            roomPriceSek = screening.getRoom().getPriceSek();
            roomPriceUsd = screening.getRoom().getPriceUsd();
            roomEquipment = screening.getRoom().getStandardEquipment();
        }

        // === Speaker ===
        String speakerName = "----";
        if (screening.getSpeakerName() != null && !screening.getSpeakerName().isBlank()) {
            speakerName = screening.getSpeakerName();
        }

        // === Movie ===
        Long movieId = null;
        String movieTitle = "----";
        String genre = "----";
        int ageLimit = 0;
        int movieDuration = 0;

        if (screening.getMovie() != null) {
            movieId = screening.getMovie().getId();
            if (screening.getMovie().getTitle() != null)
                movieTitle = screening.getMovie().getTitle();
            genre = screening.getMovie().getGenre();
            ageLimit = screening.getMovie().getAgeLimit();
            movieDuration = screening.getMovie().getDuration();
        }

        // Returnera en null-säkrad DTO
        return new AdminScreeningResponseDto(
                screening.getId(),
                screening.getStartTime(),
                screening.getEndTime(),
                screening.getPriceSek(),
                screening.getPriceUsd(),
                roomId,
                roomName,
                maxGuests,
                roomPriceSek,
                roomPriceUsd,
                roomEquipment,
                speakerName,
                movieId,
                movieTitle,
                genre,
                ageLimit,
                movieDuration
        );
    }
}
