package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.mapper.MovieMapper;
import com.leander.cinema.repository.BookingRepository;
import com.leander.cinema.repository.MovieRepository;
import com.leander.cinema.repository.ScreeningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;

    public MovieService(MovieRepository movieRepository,
                        ScreeningRepository screeningRepository,
                        BookingRepository bookingRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
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

    @Transactional(readOnly = true)
    public AdminMovieResponseDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Film med id " + id + " hittades inte"));
        return MovieMapper.toAdminMovieResponseDto(movie);
    }

    @Transactional
    public AdminMovieResponseDto createMovie(AdminMovieRequestDto body) {
        Movie movie = MovieMapper.toMovieEntity(body);
        movieRepository.save(movie);
        logger.info("Admin skapade film {}", movie.getId());
        return MovieMapper.toAdminMovieResponseDto(movie);
    }

    @Transactional
    public boolean deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isPresent()) {
            movieRepository.delete(movie.get());
            logger.info("Admin tog bort film {}", id);
            return true;
        }
        return false;
    }

}
