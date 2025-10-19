package com.leander.cinema.service;

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

    //Hjälpmetod för beräkning av biljettpris
    public BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();
            if (booking.getSpeakerName() != null) {
                // Bokning med egen talare + room
                return booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
            } else if (booking.getScreening() != null) {
                // Bokning med screening + room
                BigDecimal screeningPrice = booking.getScreening().getPriceSek();
                BigDecimal roomShare = booking.getRoom().getPriceSek()
                        .divide(BigDecimal.valueOf(booking.getRoom().getMaxGuests()), 2, RoundingMode.HALF_UP);
                return screeningPrice.add(roomShare);
            }
        } else if (ticket.getScreening() != null) {
            // Ticket direkt till screening (utan booking)
            BigDecimal screeningPrice = ticket.getScreening().getPriceSek();
            return ticket.getScreening().getPriceSek();
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
            throw new IllegalArgumentException("Ange endast en bokning eller en föreställning, aldrig båda");
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
                            HttpStatus.NOT_FOUND, "Föreställning hittades inte"));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Du måste ange antingen en bokning eller en föreställning");
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

        String speakerName = null;
        if (booking != null) {
            speakerName = booking.getSpeakerName();
        }

        String roomName = null;
        if (booking != null && booking.getRoom() != null) {
            roomName = booking.getRoom().getName();
        } else if (screening != null && screening.getRoom() != null) {
            roomName = screening.getRoom().getName();
        }

        ScreeningResponseDto screeningDto = null;
        if (screening != null) {
            // Ticket köpt direkt till en screening
            screeningDto = ScreeningMapper.toScreeningResponseDto(newTicket);
        } else if (booking != null && booking.getScreening() != null) {
            // Ticket köpt till en bokning som har en screening
            screeningDto = ScreeningMapper.toScreeningResponseDto(booking.getScreening());
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
                speakerName,
                roomName
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
            String roomName = null;
            String speakerName = null;

            if (ticket.getScreening() != null) {
                // Om det finns screening, visa screening info
                screeningDto = ScreeningMapper.toScreeningResponseDto(ticket);
                // Rummet kommer från Screening
                roomName = ticket.getScreening().getRoom().getName();

            } else if (ticket.getBooking() != null) {
                // Rummet kommer från Booking
                roomName = ticket.getBooking().getRoom().getName();
                speakerName = ticket.getBooking().getSpeakerName();
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
                    speakerName,
                    roomName
            );
            responseList.add(responseDto);
        }
        return responseList;
    }
}
