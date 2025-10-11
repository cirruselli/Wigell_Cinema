package com.leander.cinema.dto.AdminDto.movieDto;

import jakarta.validation.constraints.NotBlank;

public record AdminMovieResponseDto(
        Long id,
        String title,
        String genre,
        int ageLimit,
        String duration
) {
}
