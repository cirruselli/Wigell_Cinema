package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.BookingCapacityExceededException;
import com.leander.cinema.exception.BookingConflictException;
import com.leander.cinema.exception.InvalidBookingException;
import com.leander.cinema.mapper.BookingMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          ScreeningRepository screeningRepository,
                          AppUserRepository appUserRepository,
                          CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
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


    // === Kunden reserverar lokal -> bokning skapas ===
    @Transactional
    public BookingResponseDto createBooking(BookingPostRequestDto body) {
        Customer customer = getLoggedInCustomer();

        // Säkerställ att endast ett av alternativen används
        if (body.speakerName() != null && body.screeningId() != null) {
            throw new InvalidBookingException("Ange antingen talarens namn ELLER föreställning, inte båda.");
        }

        // Säkerställ att minst ett av alternativen finns
        if ((body.speakerName() == null || body.speakerName().isBlank()) && body.screeningId() == null) {
            throw new InvalidBookingException("Du måste ange antingen talarens namn eller föreställning.");
        }


        Booking booking = BookingMapper.toBookingEntity(body);
        booking.setCustomer(customer);

        //Kontrollera att reservationens slut inte är före start
        if (body.reservationEndTime().isBefore(body.reservationStartTime())) {
            throw new BookingConflictException("Reservationens slutdatum/tid kan inte vara före startdatum/tid.");
        }

        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());

        // Val av rum
        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte"));
        booking.setRoom(room);

        if (body.screeningId() != null) {
            // --- Scenario: Screening ---
            Screening screening = screeningRepository.findById(body.screeningId())
                    .orElseThrow(() -> new EntityNotFoundException("Föreställningen med id " + body.screeningId() + " hittades inte"));

            // Kontrollera att bokningens tid inte överlappar visningens tid
            boolean overlapsScreening = body.reservationStartTime().isBefore(screening.getEndTime()) &&
                    body.reservationEndTime().isAfter(screening.getStartTime());

            if (overlapsScreening) {
                throw new BookingConflictException("Bokningens tid överlappar föreställningens tid (" +
                        screening.getStartTime() + " - " + screening.getEndTime() + ").");
            }

            booking.setScreening(screening);
            booking.setSpeakerName(null);

        } else {
            // --- Scenario: Rum + speaker ---
            if (body.speakerName() == null || body.speakerName().isBlank()) {
                throw new InvalidBookingException("Talarnamn måste anges vid rumsbokning utan visning.");
            }
            booking.setSpeakerName(body.speakerName().trim());
        }

        if (body.numberOfGuests() > room.getMaxGuests()) {
            throw new BookingCapacityExceededException("Antal gäster överstiger rummets kapacitet på " + room.getMaxGuests() + " gäster");
        }

        if (bookingRepository.overlaps(room, body.reservationStartTime(), body.reservationEndTime())) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden av en annan bokning.");
        }

        // Ny kontroll mot screenings
        List<Screening> conflictingScreenings = screeningRepository
                .findByRoomAndTimeOverlap(room, body.reservationStartTime(), body.reservationEndTime());

        if (!conflictingScreenings.isEmpty()) {
            throw new BookingConflictException("Rummet är upptaget av en föreställning under den valda tiden.");
        }


        // Totalpris
        BigDecimal factor = new BigDecimal("0.11");
        BigDecimal totalPriceSek = room.getPriceSek();
        BigDecimal totalPriceUsd = totalPriceSek.multiply(factor);

        if (booking.getScreening() != null) {
            totalPriceSek = totalPriceSek.add(booking.getScreening().getPriceSek());
            totalPriceUsd = totalPriceUsd.add(booking.getScreening().getPriceSek().multiply(factor));
        }

        booking.setTotalPriceSek(totalPriceSek);
        booking.setTotalPriceUsd(totalPriceUsd);

        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(booking);
    }

    // === Kunden uppdaterar bokning ===
    @Transactional
    public BookingResponseDto updateBooking(Long bookingId, BookingPatchRequestDto body) {
        Customer customer = getLoggedInCustomer();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokningen hittades inte"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Du kan bara uppdatera dina egna bokningar.");
        }

        if (body.reservationStartTime() != null) {
            booking.setReservationStartTime(body.reservationStartTime());
        }
        if (body.reservationEndTime() != null) {
            booking.setReservationEndTime(body.reservationEndTime());
        }

        if (booking.getReservationEndTime().isBefore(booking.getReservationStartTime())) {
            throw new BookingConflictException("Slutdatum/tid kan inte vara före startdatum/tid.");
        }

        if (body.equipment() != null) {
            booking.getRoom().setStandardEquipment(body.equipment());
        }

        Room room = booking.getRoom();

        //Rums-krock
        boolean conflict = bookingRepository.overlapsForUpdate(
                room,
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                bookingId
        );

        if (conflict) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden.");
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

}
