package com.leander.cinema.service;

import com.leander.cinema.currency.CurrencyCalculator;
import com.leander.cinema.currency.CurrencyConverter;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketBookingResponseContentDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketResponseContent;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketScreeningResponseContentDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.ForbiddenTicketAccessException;
import com.leander.cinema.mapper.MovieMapper;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final CurrencyConverter currencyConverter;

    public TicketService(TicketRepository ticketRepository,
                         CustomerRepository customerRepository,
                         AppUserRepository appUserRepository,
                         BookingRepository bookingRepository,
                         ScreeningRepository screeningRepository,
                         CurrencyConverter currencyConverter) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.appUserRepository = appUserRepository;
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
        this.currencyConverter = currencyConverter;
    }


    //Hjälpmetod för inlogg
    public Customer getLoggedInCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Användare måste vara inloggad");
        }

        String username = authentication.getName();

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Inloggad användare hittades inte"));

        return customerRepository.findByAppUser(appUser)
                .orElseThrow(() -> new RuntimeException("Kunden kopplad till användare hittades inte"));
    }


    @Transactional
    public TicketResponseContent buyTicket(TicketRequestDto body) {

        Customer customer = getLoggedInCustomer();

        if (body.bookingId() != null && body.screeningId() != null) {
            throw new IllegalArgumentException("Ange endast en bokad föreställning eller en filmvisning, aldrig båda");
        }

        Booking booking = null;
        Screening screening = null;

        if (body.bookingId() != null) {
            booking = bookingRepository.findById(body.bookingId())
                    .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + body.bookingId() + " hittades inte"));

            // --- Kontrollera att bokningen är aktiv ---
            if (booking.getStatus() != BookingStatus.ACTIVE) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ange endast en bokad föreställning eller en filmvisning, aldrig båda"
                );
            }
        } else if (body.screeningId() != null) {
            screening = screeningRepository.findById(body.screeningId())
                    .orElseThrow(() -> new EntityNotFoundException("Föreställningen med id " + body.screeningId() + " hittades inte"));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Du måste ange antingen en bokad föreställning eller en filmvisning");
        }

        Ticket newTicket = new Ticket();
        newTicket.setCustomer(customer);
        newTicket.setBooking(booking);
        newTicket.setScreening(screening);
        newTicket.setNumberOfTickets(body.numberOfTickets());


        // --- Sätt pris per biljett och totalpris ---
        BigDecimal priceSek = CurrencyCalculator.calculateTicketPrice(newTicket);
        BigDecimal priceUsd = currencyConverter.toUsd(priceSek);
        newTicket.setPriceSek(priceSek);
        newTicket.setPriceUsd(priceUsd);
        newTicket.setTotalPriceSek(priceSek.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));
        newTicket.setTotalPriceUsd(priceUsd.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));

        ticketRepository.save(newTicket);
        logger.info("Kund {} köpte biljett {}", customer.getId(), newTicket.getId());

        // --- Returnera rätt DTO beroende på typ ---
        if (screening != null) {
            ScreeningResponseDto screeningDto = ScreeningMapper.toScreeningResponseDto(newTicket);
            return new TicketScreeningResponseContentDto(
                    newTicket.getId(),
                    newTicket.getCustomer().getFirstName(),
                    newTicket.getCustomer().getLastName(),
                    newTicket.getNumberOfTickets(),
                    priceSek,
                    priceUsd,
                    newTicket.getTotalPriceSek(),
                    newTicket.getTotalPriceUsd(),
                    screeningDto
            );
        } else if (booking != null) {
            MovieResponseDto movieDto = null;
            if (booking.getMovie() != null) {
                movieDto = MovieMapper.toMovieResponseDto(booking.getMovie());
            }

            return new TicketBookingResponseContentDto(
                    newTicket.getId(),
                    newTicket.getCustomer().getFirstName(),
                    newTicket.getCustomer().getLastName(),
                    newTicket.getNumberOfTickets(),
                    newTicket.getPriceSek(),
                    newTicket.getPriceUsd(),
                    newTicket.getTotalPriceSek(),
                    newTicket.getTotalPriceUsd(),
                    newTicket.getBooking().getReservationStartTime(),
                    newTicket.getBooking().getReservationEndTime(),
                    newTicket.getBooking().getRoom().getName(),
                    newTicket.getBooking().getSpeakerName(),
                    movieDto
            );

        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Du måste ange antingen en bokad föreställning eller en filmvisning"
        );
    }

    @Transactional(readOnly = true)
    public List<TicketResponseContent> getTicketsByCustomer(Long customerId) {
        Customer loggedInCustomer = getLoggedInCustomer();

        if (!loggedInCustomer.getId().equals(customerId)) {
            throw new ForbiddenTicketAccessException("Du kan endast se dina egna biljetter");
        }

        List<Ticket> tickets = ticketRepository.findByCustomerId(customerId);
        List<TicketResponseContent> responseList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getScreening() != null) {
                ScreeningResponseDto screeningResponseDto = ScreeningMapper.toScreeningResponseDto(ticket.getScreening());

                TicketScreeningResponseContentDto screeningTicket = new TicketScreeningResponseContentDto(
                        ticket.getId(),
                        ticket.getCustomer().getFirstName(),
                        ticket.getCustomer().getLastName(),
                        ticket.getNumberOfTickets(),
                        ticket.getPriceSek(),
                        ticket.getPriceUsd(),
                        ticket.getTotalPriceSek(),
                        ticket.getTotalPriceUsd(),
                        screeningResponseDto
                );
                responseList.add(screeningTicket);

            } else if (ticket.getBooking() != null) {
                Booking booking = ticket.getBooking();
                MovieResponseDto movieDto = null;
                if (booking.getMovie() != null) {
                    movieDto = MovieMapper.toMovieResponseDto(booking.getMovie());
                }

                TicketBookingResponseContentDto bookingTicket = new TicketBookingResponseContentDto(
                        ticket.getId(),
                        ticket.getCustomer().getFirstName(),
                        ticket.getCustomer().getLastName(),
                        ticket.getNumberOfTickets(),
                        ticket.getPriceSek(),
                        ticket.getPriceUsd(),
                        ticket.getTotalPriceSek(),
                        ticket.getTotalPriceUsd(),
                        booking.getReservationStartTime(),
                        booking.getReservationEndTime(),
                        booking.getRoom().getName(),
                        booking.getSpeakerName(),
                        movieDto
                );

                responseList.add(bookingTicket);
            }
        }

        return responseList;
    }
}
