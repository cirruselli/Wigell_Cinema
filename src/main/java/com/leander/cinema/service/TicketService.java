package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingTicketResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketResponseDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.ForbiddenTicketAccessException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;

    public TicketService(TicketRepository ticketRepository,
                         CustomerRepository customerRepository,
                         AppUserRepository appUserRepository,
                         BookingRepository bookingRepository,
                         ScreeningRepository screeningRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.appUserRepository = appUserRepository;
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
    }

    //Hjälpmetod för att beräkna biljettpris
    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();

            // Bokning med egen talare + rum
            if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                return booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
            }

            // Bokning med film + rum
            if (booking.getMovie() != null) {
                BigDecimal roomPricePerGuest = booking.getRoom().getPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);

                return roomPricePerGuest;
            }
        }
        return BigDecimal.ZERO;
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
    public TicketResponseDto buyTicket(TicketRequestDto body) {

        Customer customer = getLoggedInCustomer();

        // Säkerställ att endast ett ID är angivet
        if (body.bookingId() != null && body.screeningId() != null) {
            throw new IllegalArgumentException("Ange endast en bokad föreställning eller en filmvisning, aldrig båda");
        }

        Booking booking = null;
        Screening screening = null;

        if (body.bookingId() != null) {
            booking = bookingRepository.findById(body.bookingId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Bokning hittades inte"));
        } else if (body.screeningId() != null) {
            screening = screeningRepository.findById(body.screeningId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Föreställningen hittades inte"));
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
        BigDecimal priceSek = calculateTicketPrice(newTicket); // SEK
        BigDecimal priceUsd = priceSek.multiply(BigDecimal.valueOf(0.11)).setScale(2, RoundingMode.HALF_UP);
        newTicket.setPriceSek(priceSek);
        newTicket.setPriceUsd(priceUsd);
        newTicket.setTotalPriceSek(priceSek.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));
        newTicket.setTotalPriceUsd(priceUsd.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));

        ticketRepository.save(newTicket);

        // --- Skapa föreställning/film DTO ---
        ScreeningResponseDto screeningDto = null;
        BookingTicketResponseDto bookingDto = null;

        if (screening != null) {
            // Ticket direkt till föreställning
            screeningDto = ScreeningMapper.toScreeningResponseDto(newTicket);
        } else if (booking != null) {
            // Ticket kopplad till bokning
            bookingDto = new BookingTicketResponseDto(
                    newTicket.getBooking().getReservationStartTime(),
                    newTicket.getBooking().getReservationEndTime(),
                    newTicket.getBooking().getRoom().getName(),
                    newTicket.getBooking().getSpeakerName()
            );
        }

        return new TicketResponseDto(
                newTicket.getId(),
                newTicket.getCustomer().getFirstName(),
                newTicket.getCustomer().getLastName(),
                newTicket.getNumberOfTickets(),
                priceSek,
                priceUsd,
                newTicket.getTotalPriceSek(),
                newTicket.getTotalPriceUsd(),
                screeningDto,
                bookingDto
        );
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDto> getTicketsByCustomer(Long customerId) {
        Customer loggedInCustomer = getLoggedInCustomer();

        if (!loggedInCustomer.getId().equals(customerId)) {
            throw new ForbiddenTicketAccessException("Du kan endast se dina egna biljetter");
        }

        List<Ticket> tickets = ticketRepository.findByCustomerId((customerId));
        List<TicketResponseDto> responseList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            ScreeningResponseDto screeningDto = null;
            BookingTicketResponseDto bookingDto = null;

            if (ticket.getScreening() != null) {
                screeningDto = ScreeningMapper.toScreeningResponseDto(ticket);

            } else if (ticket.getBooking() != null) {
                bookingDto = new BookingTicketResponseDto(
                        ticket.getBooking().getReservationStartTime(),
                        ticket.getBooking().getReservationEndTime(),
                        ticket.getBooking().getRoom().getName(),
                        ticket.getBooking().getSpeakerName()
                );
            }

            TicketResponseDto responseDto = new TicketResponseDto(
                    ticket.getId(),
                    ticket.getCustomer().getFirstName(),
                    ticket.getCustomer().getLastName(),
                    ticket.getNumberOfTickets(),
                    ticket.getPriceSek(),
                    ticket.getPriceUsd(),
                    ticket.getTotalPriceSek(),
                    ticket.getTotalPriceUsd(),
                    screeningDto,
                    bookingDto
            );
            responseList.add(responseDto);
        }
        return responseList;
    }
}
