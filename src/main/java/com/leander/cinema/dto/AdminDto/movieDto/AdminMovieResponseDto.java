package com.leander.cinema.dto.AdminDto.movieDto;

public record AdminMovieResponseDto(
        Long id,
        String title,
        String genre,
        int ageLimit,
        int duration
) {
}
