package com.leander.cinema.dto.CustomerDto.movie;

public record MovieResponseDto(
        String title,
        String genre,
        int ageLimit,
        double duration) {
}