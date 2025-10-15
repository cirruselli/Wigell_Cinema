package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.BookingCapacityExceededException;
import com.leander.cinema.exception.BookingConflictException;
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

        //ÄNDRA DATUM UTIFRÅN ATT BOKNING INTE HAR DATUM OCH ATT SCREENING HAR DATUM SOM SKA KONTROLLERAS

        if (body.reservationEndTime().isBefore(body.reservationStartTime())) {
            throw new BookingConflictException("Reservationens slutdatum/tid kan inte vara före startdatum/tid.");
        }

        if (bookingRepository.existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThan(
                room, body.reservationEndTime(), body.reservationStartTime())) {
            throw new BookingConflictException("Rummet är upptaget under den valda tiden.");
        }

        Booking booking = BookingMapper.toBookingEntity(body);
        //Bokningen kopplas till den inloggade kunden
        booking.setCustomer(currentUser.getCustomer());

        //Beräkna totalpris
        BigDecimal factor = new BigDecimal("0.11");

        //SEK
        BigDecimal totalPriceSek = room.getPriceSek().add(screening.getPriceSek());
        booking.setTotalPriceSek(totalPriceSek);

        //USD
        BigDecimal totalPriceUsd = room.getPriceSek().add(screening.getPriceSek());
        totalPriceUsd = totalPriceUsd.multiply(factor);

        booking.setTotalPriceSek(totalPriceSek);
        booking.setTotalPriceUsd(totalPriceUsd);
        booking.setRoom(room);
        booking.setScreening(screening);

        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(booking);
    }
}
