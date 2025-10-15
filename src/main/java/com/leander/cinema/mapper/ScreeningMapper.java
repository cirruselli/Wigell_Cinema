package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.entity.Screening;

import java.math.BigDecimal;
import java.util.List;

public class ScreeningMapper {
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
        Long speakerId = null;
        String speakerName = "";
        BigDecimal speakerPriceSek = null;
        BigDecimal speakerPriceUsd = null;
        int speakerDuration = 0;

        if (screening.getSpeaker() != null) {
            speakerId = screening.getSpeaker().getId();
            if (screening.getSpeaker().getName() != null)
                speakerName = screening.getSpeaker().getName();
            speakerPriceSek = screening.getSpeaker().getPriceSek();
            speakerPriceUsd = screening.getSpeaker().getPriceUsd();
            speakerDuration = screening.getSpeaker().getDuration();
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
                speakerId,
                speakerName,
                speakerPriceSek,
                speakerPriceUsd,
                speakerDuration,
                movieId,
                movieTitle,
                genre,
                ageLimit,
                movieDuration
        );
    }
}
