package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Movie;

public class MovieMapper {
    public static MovieResponseDto toMovieResponseDto(Movie movie){
        return new MovieResponseDto(movie.getTitle(), movie.getGenre(), movie.getAgeLimit(), movie.getDuration());
    }

    public static AdminMovieResponseDto toAdminMovieResponseDto(Movie movie){
        return new AdminMovieResponseDto(
                movie.getId(),
                movie.getTitle().trim(),
                movie.getGenre().trim(),
                movie.getAgeLimit(),
                movie.getDuration());
    }
}
