package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.mapper.MovieMapper;
import com.leander.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieResponseDto> getAllMoviesForCustomer() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieResponseDto> responseList = new ArrayList<>();
        for (Movie movie : movies) {
            MovieResponseDto movieResponse = MovieMapper.toMovieResponseDto(movie);
            responseList.add(movieResponse);
        }
        return responseList;
    }

    public List<AdminMovieResponseDto> getAllMoviesForAdmin() {
        List<Movie> movies = movieRepository.findAll();

        List<AdminMovieResponseDto> responseList = new ArrayList<>();
        for (Movie movie : movies) {
            AdminMovieResponseDto movieResponse = MovieMapper.toAdminMovieResponseDto(movie);
            responseList.add(movieResponse);
        }
        return responseList;
    }
}
