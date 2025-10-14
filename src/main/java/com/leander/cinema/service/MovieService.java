package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Address;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.mapper.MovieMapper;
import com.leander.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> getAllMoviesForCustomer() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieResponseDto> responseList = new ArrayList<>();
        for (Movie movie : movies) {
            MovieResponseDto movieResponse = MovieMapper.toMovieResponseDto(movie);
            responseList.add(movieResponse);
        }
        return responseList;
    }

    @Transactional(readOnly = true)
    public List<AdminMovieResponseDto> getAllMoviesForAdmin() {
        List<Movie> movies = movieRepository.findAll();

        List<AdminMovieResponseDto> responseList = new ArrayList<>();
        for (Movie movie : movies) {
            AdminMovieResponseDto movieResponse = MovieMapper.toAdminMovieResponseDto(movie);
            responseList.add(movieResponse);
        }
        return responseList;
    }
    @Transactional
    public AdminMovieResponseDto createMovie(AdminMovieRequestDto body) {
        Movie movie = MovieMapper.toMovieEntity(body);
        return MovieMapper.toAdminMovieResponseDto(movieRepository.save(movie));
    }

    public boolean deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isPresent()) {
            movieRepository.delete(movie.get());
            return true;
        }
        return false;
    }
}
