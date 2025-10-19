package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;

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

    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        // Room
        String roomName = booking.getRoom().getName();
        int maxGuests = booking.getRoom().getMaxGuests();

        // Kopiera standardutrustningen från rummet (om den finns)
        List<String> equipments = new ArrayList<>();
        if (booking.getRoom() != null && booking.getRoom().getStandardEquipment() != null) {
            equipments.addAll(booking.getRoom().getStandardEquipment());
        }

        // Om bokningen har egen utrustning, använd den istället (överlagrar rummet)
        if (booking.getRoomEquipment() != null) {
            equipments = new ArrayList<>(booking.getRoomEquipment()); // kopiera så att vi inte ändrar ursprungsrummet
        }


        // Speaker
        String speakerName = "----";
        if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
            speakerName = booking.getSpeakerName();
        }

        // Movie
        MovieResponseDto movieDto = null;
        if (booking.getMovie() != null) {
            movieDto = MovieMapper.toMovieResponseDto(booking.getMovie());
        }

        // Customer
        String customerFirstName = booking.getCustomer().getFirstName();
        String customerLastName = booking.getCustomer().getLastName();

        return new BookingResponseDto(
                booking.getId(),
                booking.getStatus(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd(),
                roomName,
                equipments,
                maxGuests,
                speakerName,
                movieDto,
                customerFirstName,
                customerLastName
        );
    }

    public static AdminBookingResponseDto toAdminBookingResponseDto(Booking booking) {

        AdminRoomResponseDto roomDto = null;
        if (booking.getRoom() != null) {
            Room room = booking.getRoom();
            roomDto = new AdminRoomResponseDto(
                    room.getId(),
                    room.getName(),
                    room.getMaxGuests(),
                    room.getPriceSek(),
                    room.getPriceUsd(),
                    room.getStandardEquipment()
            );
        }

        // Movie
        AdminMovieResponseDto movieDto = null;
        if (booking.getMovie() != null) {
            movieDto = MovieMapper.toAdminMovieResponseDto(booking.getMovie());
        }

        return new AdminBookingResponseDto(
                booking.getId(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                booking.getRoomEquipment(),
                roomDto,
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd(),
                booking.getSpeakerName(),
                movieDto,
                booking.getStatus()
        );
    }
}