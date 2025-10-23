package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.ticketDto.TicketBookingResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketResponseDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.ForbiddenTicketAccessException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class TicketService {
    Logger logger = LoggerFactory.getLogger(TicketService.class);


    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final CurrencyConverterClient currencyConverter;

    public TicketService(TicketRepository ticketRepository,
                         CustomerRepository customerRepository,
                         AppUserRepository appUserRepository,
                         BookingRepository bookingRepository,
                         ScreeningRepository screeningRepository,
                         CurrencyConverterClient currencyConverter) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.appUserRepository = appUserRepository;
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
        this.currencyConverter = currencyConverter;
    }

    //Hjälpmetod för att beräkna biljettpris
    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();
            if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                return booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
            }
            if (booking.getMovie() != null) {
                BigDecimal roomPricePerGuest = booking.getRoom().getPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
                return roomPricePerGuest;
            }
        }
        if (ticket.getScreening() != null) {
            Screening screening = ticket.getScreening();
            BigDecimal roomPricePerGuest = screening.getPriceSek();
            return roomPricePerGuest;
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
        BigDecimal priceUsd = currencyConverter.convertSekToUsd(priceSek);
        newTicket.setPriceSek(priceSek);
        newTicket.setPriceUsd(priceUsd);
        newTicket.setTotalPriceSek(priceSek.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));
        newTicket.setTotalPriceUsd(priceUsd.multiply(BigDecimal.valueOf(newTicket.getNumberOfTickets())));

        ticketRepository.save(newTicket);
        logger.info("Användare {} köpte biljett {}", customer.getId(), newTicket.getId());

        // --- Skapa föreställning/film DTO ---
        ScreeningResponseDto screeningDto = null;
        TicketBookingResponseDto bookingDto = null;

        if (screening != null) {
            // Ticket direkt till föreställning
            screeningDto = ScreeningMapper.toScreeningResponseDto(newTicket);
        } else if (booking != null) {
            // Ticket kopplad till bokning
            bookingDto = new TicketBookingResponseDto(
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
            TicketBookingResponseDto bookingDto = null;

            if (ticket.getScreening() != null) {
                screeningDto = ScreeningMapper.toScreeningResponseDto(ticket);

            }
            if (ticket.getBooking() != null) {
                bookingDto = new TicketBookingResponseDto(
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
