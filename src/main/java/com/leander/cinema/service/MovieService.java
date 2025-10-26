package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.MovieDeletionException;
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
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filmen med id " + id + " hittades inte"));

        // --- Kontrollera att inga screenings finns kvar ---
        List<Screening> screenings = screeningRepository.findByMovie(movie);
        if (screenings != null && !screenings.isEmpty()) {
            throw new MovieDeletionException("Filmen med id " + id + " kan inte tas bort eftersom det finns föreställningar på filmen");
        }

        // --- Kontrollera att inga aktiva bokningar finns ---
        List<Booking> bookings = bookingRepository.findByMovie(movie);
        boolean hasActiveBookings = false;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.getStatus() == BookingStatus.ACTIVE) {
                    hasActiveBookings = true;
                    break;
                }
            }
        }

        if (hasActiveBookings) {
            throw new MovieDeletionException("Filmen med id " + id + "  kan inte tas bort eftersom det finns aktiva bokningar som använder filmen");
        }

        // --- Frikoppla completed bokningar (snapshot) ---
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
                    booking.setMovie(null);
                    booking.setMovieTitle(movie.getTitle());
                    booking.setMovieGenre(movie.getGenre());
                    booking.setMovieAgeLimit(movie.getAgeLimit());
                    booking.setMovieDuration(movie.getDuration());
                }
            }
            bookingRepository.saveAll(bookings);
        }

        // --- Ta bort filmen ---
        movieRepository.delete(movie);
        logger.info("Admin tog bort film {}", id);
    }
}