package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.BookingCapacityExceededException;
import com.leander.cinema.exception.BookingConflictException;
import com.leander.cinema.exception.InvalidBookingException;
import com.leander.cinema.mapper.BookingMapper;
import com.leander.cinema.repository.AppUserRepository;
import com.leander.cinema.repository.BookingRepository;
import com.leander.cinema.repository.RoomRepository;
import com.leander.cinema.repository.ScreeningRepository;
import com.leander.cinema.security.AppUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final AppUserRepository appUserRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, ScreeningRepository screeningRepository, AppUserRepository appUserRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
        this.appUserRepository = appUserRepository;
    }

    //Kunden reserverar lokal
    @Transactional
    public BookingResponseDto createBooking(BookingPostRequestDto body) {
        //Hämta inloggad användare
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser currentUser = appUserRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Ingen användare hittades"));

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte"));

        if (body.numberOfGuests() > room.getMaxGuests()) {
            throw new BookingCapacityExceededException("Antal gäster överstiger rummets kapacitet på " + room.getMaxGuests() + " gäster");
        }

        Screening screening = screeningRepository.findById(body.screeningId())
                .orElseThrow(() -> new EntityNotFoundException("Föreställningen med id " + body.screeningId() + " hittades inte"));

        Booking booking = BookingMapper.toBookingEntity(body);
        booking.setScreening(screening);
        booking.setRoom(room);
        //Bokningen kopplas till inloggad användare
        booking.setCustomer(currentUser.getCustomer());

        // Tider för bokningen sätts av användaren
        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());

        //Kontrollera att reservationens slut inte är före start
        if (body.reservationEndTime().isBefore(body.reservationStartTime())) {
            throw new BookingConflictException("Reservationens slutdatum/tid kan inte vara före startdatum/tid.");
        }

        // === Kontrollera krock med andra bokningar i samma rum ===

        //Rummet är inte redan bokat under tiden
        boolean roomBookingConflict = bookingRepository.existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThan(
                room, booking.getReservationEndTime(), booking.getReservationStartTime());

        if (roomBookingConflict) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden.");
        }

        //Det finns ingen annan screening i samma rum som krockar i tid
        boolean screeningConflict = screeningRepository.existsScreeningInRoomDuring(
                booking.getRoom().getId(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getScreening().getId()
        );

        if (screeningConflict) {
            throw new BookingConflictException("Rummet är redan upptaget av en annan föreställning under den valda tiden.");
        }

        //Samma film får inte visas parallellt i olika rum
        if (screening.getMovie() != null) { // endast för filmvisningar
            boolean sameMovieOverlap = screeningRepository.existsByMovieIdAndTimeOverlap(
                    booking.getScreening().getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime()
            );

            if (sameMovieOverlap) {
                throw new InvalidBookingException("Filmen visas redan under denna tid i en annan salong.");
            }
        }


        // === Beräkna totalpris ===
        BigDecimal factor = new BigDecimal("0.11");

        //SEK
        BigDecimal totalPriceSek = room.getPriceSek().add(screening.getPriceSek());

        //USD
        BigDecimal totalPriceUsd = room.getPriceSek().add(screening.getPriceSek());
        totalPriceUsd = totalPriceUsd.multiply(factor);

        booking.setTotalPriceSek(totalPriceSek);
        booking.setTotalPriceUsd(totalPriceUsd);

        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto updateBooking(Long bookingId, BookingPatchRequestDto body) {
        //Hämta inloggad användare
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser currentUser = appUserRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Ingen användare hittades"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokningen hittades inte"));

        if (!booking.getCustomer().getId().equals(currentUser.getCustomer().getId())) {
            throw new AccessDeniedException("Du kan bara uppdatera dina egna bokningar.");
        }

        if (body.reservationStartTime() != null) {
            booking.setReservationStartTime(body.reservationStartTime());
        }
        if (body.reservationEndTime()!= null) {
            booking.setReservationEndTime(body.reservationEndTime());
        }

        if (body.equipment() != null) {
            booking.getRoom().setStandardEquipment(body.equipment());
        }

        Room room = booking.getRoom();

        //Rums-krock
        if (bookingRepository.existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThanAndIdNot(
                room, booking.getReservationEndTime(), booking.getReservationStartTime(), bookingId)) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden.");
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

}
