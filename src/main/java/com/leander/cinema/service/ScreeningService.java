package com.leander.cinema.service;

import com.leander.cinema.currency.CurrencyConverter;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.entity.Ticket;
import com.leander.cinema.exception.BookingConflictException;
import com.leander.cinema.exception.InvalidScreeningException;
import com.leander.cinema.exception.ScreeningDeletionException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScreeningService {
    Logger logger = LoggerFactory.getLogger(ScreeningService.class);

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final CurrencyConverter currencyConverter;

    public ScreeningService(ScreeningRepository screeningRepository,
                            RoomRepository roomRepository,
                            MovieRepository movieRepository,
                            BookingRepository bookingRepository,
                            TicketRepository ticketRepository,
                            CurrencyConverter currencyConverter) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.currencyConverter = currencyConverter;
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

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte."));


        Movie movie = movieRepository.findById(body.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Film med id " + body.movieId() + " hittades inte."));

        Screening screening = ScreeningMapper.toScreeningEntity(body);
        screening.setRoom(room);
        screening.setMovie(movie);
        screening.setEndTime(body.startTime().plusMinutes(movie.getDuration()));

        // Filmens längd + 30 minuter
        screening.setTotalEndTime(screening.getEndTime().plusMinutes(30));

        //Kontroller av tiderna
        if (screening.getEndTime().isBefore(body.startTime())) {
            throw new InvalidScreeningException("Sluttiden kan inte vara före starttiden.");
        }

        // Kontrollera om det finns någon bokning i samma rum som krockar med föreställningens totala tid
        boolean conflictWithBooking = bookingRepository.overlaps(
                room,
                body.startTime(),
                screening.getTotalEndTime()
        );
        if (conflictWithBooking) {
            throw new BookingConflictException(
                    "Föreställningen krockar med en bokning i samma rum."
            );
        }

        // Kontrollera överlappningar mot andra visningar baserat på totalEndTime
        List<Screening> overlapping = screeningRepository.findByRoomAndTimeOverlap(
                room, screening.getStartTime(), screening.getTotalEndTime());

        if (!overlapping.isEmpty()) {
            throw new BookingConflictException("Föreställningen krockar med en annan visning/bokning i samma sal.");
        }

        BigDecimal priceSek = screening.getPriceSek();
        BigDecimal priceUsd = currencyConverter.toUsd(priceSek);
        screening.setPriceUsd(priceUsd);

        screeningRepository.save(screening);

        logger.info("Admin skapade föreställning {}", screening.getId());

        return ScreeningMapper.toAdminScreeningResponseDto(screening);
    }

    @Transactional
    public void deleteScreening(Long id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Föreställning hittades inte"));

        Movie movie = screening.getMovie(); // kan vara null

        // Hämta biljetter kopplade till screeningen
        List<Ticket> tickets = ticketRepository.findByScreening(screening);

        // Om det finns biljetter kopplade → kasta fel
        if (!tickets.isEmpty()) {
            throw new ScreeningDeletionException("Föreställningen kan inte tas bort eftersom det finns biljetter kopplade till den");
        }

        // Ta bort screeningen
        screeningRepository.delete(screening);

        if (movie != null) {
            logger.info("Admin tog bort föreställning {} kopplad till film '{}'", id, movie.getId());
        } else {
            logger.info("Admin tog bort föreställning {} (utan kopplad film)", id);
        }

        logger.info("Frikopplade {} biljetter från föreställning {}", tickets.size(), id);
    }
}
