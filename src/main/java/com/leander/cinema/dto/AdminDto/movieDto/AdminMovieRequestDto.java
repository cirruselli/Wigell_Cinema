package com.leander.cinema.dto.AdminDto.movieDto;

import jakarta.validation.constraints.*;

public record AdminMovieRequestDto(
        @NotBlank(message = "Filmtitel måste anges")
        @Size(max = 150, message = "Teckenlängd max 150")
        String title,
        @NotBlank(message = "Genre måste anges")
        @Size(max = 150, message = "Teckenlängd max 150")
        String genre,
        @NotNull(message = "Åldersgräns måste anges")
        int ageLimit,
        @NotBlank(message = "Längden på filmen måste anges")
        double duration
) {
}
