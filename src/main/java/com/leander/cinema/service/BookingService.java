package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.BookingCapacityExceededException;
import com.leander.cinema.exception.BookingConflictException;
import com.leander.cinema.exception.CustomerOwnershipException;
import com.leander.cinema.exception.InvalidBookingException;
import com.leander.cinema.mapper.BookingMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          ScreeningRepository screeningRepository,
                          AppUserRepository appUserRepository,
                          CustomerRepository customerRepository,
                          MovieRepository movieRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
        this.movieRepository = movieRepository;
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


    // === Kunden ser tidigare och aktiva bokningar ===
    @Transactional
    public List<BookingResponseDto> getActiveAndCompletedBookings(Long customerId) {
        Customer loggedInCustomer = getLoggedInCustomer();

        List<Booking> bookings = bookingRepository.findByCustomerId((customerId));

        if (!loggedInCustomer.getId().equals(customerId)) {
            throw new CustomerOwnershipException("Du kan bara se dina egna bokningar.");
        }

        // Lista att lägga till bokningar att visa
        List<Booking> filteredBookings = new ArrayList<>();

        // Filtrera bort oönskade statusar
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ACTIVE || booking.getStatus() == BookingStatus.COMPLETED) {
                filteredBookings.add(booking);
            }
        }

        List<BookingResponseDto> responseList = new ArrayList<>();

        for (Booking booking : filteredBookings) {
            BookingResponseDto bookingDto = BookingMapper.toBookingResponseDto(booking);
            responseList.add(bookingDto);
        }

        return responseList;
    }

    // === Kunden reserverar lokal -> bokning skapas ===
    @Transactional
    public BookingResponseDto createBooking(BookingPostRequestDto body) {
        Customer customer = getLoggedInCustomer();

        // Säkerställ att endast ett av alternativen används
        if (body.speakerName() != null && body.movieId() != null) {
            throw new InvalidBookingException("Ange antingen talarens namn ELLER film, inte båda.");
        }

        // Säkerställ att minst ett av alternativen finns
        if ((body.speakerName() == null || body.speakerName().isBlank()) && body.movieId() == null) {
            throw new InvalidBookingException("Du måste ange antingen talarens namn eller film.");
        }

        Booking booking = BookingMapper.toBookingEntity(body);
        booking.setCustomer(customer);

        //Kontrollera att reservationens slut inte är före start
        if (body.reservationEndTime().isBefore(body.reservationStartTime())) {
            throw new BookingConflictException("Reservationens slutdatum/tid kan inte vara före startdatum/tid.");
        }

        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte"));
        booking.setRoom(room);

        // --- Talare eller film ---
        if (body.movieId() != null) {
            Movie movie = movieRepository.findById(body.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Filmen med id " + body.movieId() + " hittades inte"));
            booking.setMovie(movie);
            booking.setSpeakerName(null);
        } else {
            booking.setSpeakerName(body.speakerName().trim());
        }

        // --- Kontroller ---
        if (body.numberOfGuests() > room.getMaxGuests()) {
            throw new BookingCapacityExceededException("Antal gäster överstiger rummets kapacitet på " + room.getMaxGuests());
        }

        if (bookingRepository.overlaps(room, body.reservationStartTime(), body.reservationEndTime())) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden av en annan bokning.");
        }

        // Kontroll mot pågående screenings
        List<Screening> conflictingScreenings = screeningRepository
                .findByRoomAndTimeOverlap(room, body.reservationStartTime(), body.reservationEndTime());
        if (!conflictingScreenings.isEmpty()) {
            throw new BookingConflictException("Rummet är upptaget av en föreställning under den valda tiden.");
        }

        // --- Totalpris ---
        BigDecimal factor = new BigDecimal("0.11");
        BigDecimal totalPriceSek = room.getPriceSek();
        BigDecimal totalPriceUsd = totalPriceSek.multiply(factor);

        booking.setTotalPriceSek(totalPriceSek);
        booking.setTotalPriceUsd(totalPriceUsd);
        booking.setStatus(BookingStatus.ACTIVE);

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
            throw new CustomerOwnershipException("Du kan bara uppdatera dina egna bokningar.");
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

        if (body.roomEquipment() != null) {
            booking.setRoomEquipment(new ArrayList<>(body.roomEquipment()));
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
