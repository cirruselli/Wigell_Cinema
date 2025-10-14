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
        @Min(value = 0, message = "Åldersgränsen får inte vara negativ")
        @Max(value = 21, message = "Åldersgränsen får inte vara högre än 21")
        Integer ageLimit,

        @NotNull(message = "Längden på filmen måste anges")
        @Min(value = 0, message = "Längden måste vara större än 0")
        Double duration
) {
}
