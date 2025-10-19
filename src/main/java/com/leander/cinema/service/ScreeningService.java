package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.InvalidScreeningException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.BookingRepository;
import com.leander.cinema.repository.MovieRepository;
import com.leander.cinema.repository.RoomRepository;
import com.leander.cinema.repository.ScreeningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;

    public ScreeningService(ScreeningRepository screeningRepository,
                            RoomRepository roomRepository,
                            MovieRepository movieRepository,
                            BookingRepository bookingRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
        this.bookingRepository = bookingRepository;
    }

    // === Kunden listar föreställningar ===
    @Transactional(readOnly = true)
    public List<ScreeningResponseDto> getScreeningsByMovieAndDate(Long movieId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Screening> screenings = screeningRepository.findByMovieIdAndStartTimeBetween(movieId, startOfDay, endOfDay);
        List<ScreeningResponseDto> responseList = new ArrayList<>();

        for (Screening screening : screenings) {
            ScreeningResponseDto screeningDto = ScreeningMapper.toScreeningResponseDto(screening);
            responseList.add(screeningDto);
        }

        return responseList;
    }


    @Transactional(readOnly = true)
    public List<AdminScreeningResponseDto> getAllScreeningsForAdmin() {
        List<Screening> screenings = screeningRepository.findAll();

        List<AdminScreeningResponseDto> responseList = new ArrayList<>();
        for (Screening screening : screenings) {
            AdminScreeningResponseDto screeningResponse = ScreeningMapper.toAdminScreeningResponseDto(screening);
            responseList.add(screeningResponse);
        }
        return responseList;
    }

    @Transactional
    public AdminScreeningResponseDto createScreening(AdminScreeningRequestDto body) {

        //Kontroller av tiderna
        if (body.endTime().isBefore(body.startTime())) {
            throw new InvalidScreeningException("Sluttiden kan inte vara före starttiden.");
        }

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte."));


        Movie movie = movieRepository.findById(body.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Film med id " + body.movieId() + " hittades inte."));

        // Kontrollera överlappning med bokningar
        List<Booking> overlappingBookings = bookingRepository.findByRoomAndTimeOverlap(
                room,
                body.startTime(),
                body.endTime()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new InvalidScreeningException(
                    "Rummet är redan bokat av en bokning under denna tid."
            );
        }

        // Kontrollera överlappning med andra screenings
        boolean conflict = screeningRepository.existsOverlappingScreening(
                body.roomId(),
                body.startTime(),
                body.endTime()
        );

        if (conflict) {
            throw new InvalidScreeningException("Rummet är redan bokat av en annan screening under denna tid.");
        }

        Screening screening = ScreeningMapper.toScreeningEntity(body);
        screening.setRoom(room);
        screening.setMovie(movie);

        BigDecimal factor = new BigDecimal("0.11");
        BigDecimal priceUsd = screening.getPriceSek().multiply(factor);
        screening.setPriceUsd(priceUsd);

        screeningRepository.save(screening);

        return ScreeningMapper.toAdminScreeningResponseDto(screening);
    }

    @Transactional
    public boolean deleteScreening(Long id) {
        Optional<Screening> screening = screeningRepository.findById(id);

        if (screening.isPresent()) {
            screeningRepository.delete(screening.get());
            return true;
        }
        return false;
    }
}
