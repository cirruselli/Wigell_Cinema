package com.leander.cinema.dto.AdminDto.screeningDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminScreeningResponseDto(
        Long screeningId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal priceSek,
        BigDecimal priceUsd,
        Long roomId,
        String roomName,
        int maxGuests,
        BigDecimal roomPriceSek,
        BigDecimal roomPriceUsd,
        List<String> roomEquipments,
        Long speakerId,
        String speakerName,
        BigDecimal speakerPriceSek,
        BigDecimal speakerPriceUsd,
        int speakerDuration,
        Long movieId,
        String movieTitle,
        String genre,
        int ageLimit,
        int movieDuration) {
}
