package com.leander.cinema.dto.CustomerDto.movieDto;

public record MovieResponseDto(
        String title,
        String genre,
        int ageLimit,
        int duration) {
}