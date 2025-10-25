package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingMovieResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseContent;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingSpeakerResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.*;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking toBookingEntity(BookingPostRequestDto body) {
        Booking booking = new Booking();

        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());
        booking.setNumberOfGuests(body.numberOfGuests());

        // SpeakerName med null-säker trim
        if (body.speakerName() != null) {
            booking.setSpeakerName(body.speakerName().trim());
        } else {
            booking.setSpeakerName(null);
        }

        return booking;
    }

    public static BookingResponseContent toBookingResponseDto(Booking booking) {

        // Room
        String roomName = booking.getRoom().getName();
        int maxGuests = booking.getRoom().getMaxGuests();

        // Kopiera standardutrustningen från rummet (om den finns)
        List<String> equipments = new ArrayList<>();
        if (booking.getRoom() != null && booking.getRoom().getStandardEquipment() != null) {
            equipments.addAll(booking.getRoom().getStandardEquipment());
        }

        // Om bokningen har egen utrustning, använd den istället
        if (booking.getRoomEquipment() != null) {
            equipments = new ArrayList<>(booking.getRoomEquipment()); // kopiera för att inte ändra ursprungsrummet
        }

        if (booking.getMovie() != null) {
            return new BookingMovieResponseDto(
                    booking.getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime(),
                    booking.getNumberOfGuests(),
                    booking.getTotalPriceSek(),
                    booking.getTotalPriceUsd(),
                    equipments,
                    roomName,
                    maxGuests,
                    MovieMapper.toMovieResponseDto(booking.getMovie()),
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getStatus()
            );
        }
        if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
            return new BookingSpeakerResponseDto(
                    booking.getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime(),
                    booking.getNumberOfGuests(),
                    booking.getTotalPriceSek(),
                    booking.getTotalPriceUsd(),
                    roomName,
                    equipments,
                    maxGuests,
                    booking.getSpeakerName(),
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getStatus()
            );
        }
        throw new IllegalStateException("Bokning måste ha antingen talare eller film");
    }

    public static AdminBookingResponseContent toAdminBookingResponseContent(Booking booking) {
        // --- Bygg Room-dto ---
        AdminRoomResponseDto roomDto = null;
        if (booking.getRoom() != null) {
            roomDto = new AdminRoomResponseDto(
                    booking.getRoom().getId(),
                    booking.getRoom().getName(),
                    booking.getRoom().getMaxGuests(),
                    booking.getRoom().getPriceSek(),
                    booking.getRoom().getPriceUsd(),
                    booking.getRoom().getStandardEquipment()
            );
        }

        if (booking.getMovie() != null) {
            MovieResponseDto movieDto = MovieMapper.toMovieResponseDto(booking.getMovie());
            return new AdminBookingMovieResponseDto(
                    booking.getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime(),
                    booking.getNumberOfGuests(),
                    booking.getTotalPriceSek(),
                    booking.getTotalPriceUsd(),
                    booking.getRoomEquipment(),
                    booking.getStatus(),
                    roomDto,
                    movieDto
            );
        } else if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
            return new AdminBookingSpeakerResponseDto(
                    booking.getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime(),
                    booking.getNumberOfGuests(),
                    booking.getTotalPriceSek(),
                    booking.getTotalPriceUsd(),
                    booking.getRoomEquipment(),
                    booking.getStatus(),
                    roomDto,
                    booking.getSpeakerName()
            );
        }
        throw new IllegalStateException("Bokning måste ha antingen talare eller film");
    }
}