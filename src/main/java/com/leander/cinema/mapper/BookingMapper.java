package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
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

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        // Kontrollerar att inga null-värden sätts
        // Room
        String roomName = "----";
        int maxGuests = 0;

        roomName = booking.getRoom().getName();
        maxGuests = booking.getRoom().getMaxGuests();

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
        String movieTitle = "----";
        String genre = "----";
        int ageLimit = 0;
        int duration = 0;

        if (booking.getScreening() != null && booking.getScreening().getMovie() != null) {
            var movie = booking.getScreening().getMovie();

            if (movie.getTitle() != null) {
                movieTitle = movie.getTitle();
            }
            if (movie.getGenre() != null) {
                genre = movie.getGenre();
            }
            ageLimit = movie.getAgeLimit();
            duration = movie.getDuration();
        }

        // Customer
        String customerFirstName = "----";
        String customerLastName = "----";
        if (booking.getCustomer() != null) {
            if (booking.getCustomer().getFirstName() != null) {
                customerFirstName = booking.getCustomer().getFirstName();
            }
            if (booking.getCustomer().getLastName() != null) {
                customerLastName = booking.getCustomer().getLastName();
            }
        }

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
                movieTitle,
                genre,
                ageLimit,
                duration,
                customerFirstName,
                customerLastName
        );
    }

    public static AdminBookingResponseDto toAdminBookingResponseDto(Booking booking) {
        // Bokningens rum (alltid det som gäller)
        AdminRoomResponseDto roomDto = null;
        if (booking.getRoom() != null) {
            var room = booking.getRoom();
            roomDto = new AdminRoomResponseDto(
                    room.getId(),
                    room.getName(),
                    room.getMaxGuests(),
                    room.getPriceSek(),
                    room.getPriceUsd(),
                    room.getStandardEquipment()
            );
        }

        // Screening (utan filmens rum och tider)
        AdminScreeningResponseDto screeningDto = null;
        if (booking.getScreening() != null) {
            var screening = booking.getScreening();

            AdminMovieResponseDto movieDto = null;
            if (screening.getMovie() != null) {
                var movie = screening.getMovie();
                movieDto = new AdminMovieResponseDto(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getGenre(),
                        movie.getAgeLimit(),
                        movie.getDuration()
                );
            }

            screeningDto = new AdminScreeningResponseDto(
                    screening.getId(),
                    null, // Ta bort startTime
                    null, // Ta bort endTime
                    screening.getPriceSek(),
                    screening.getPriceUsd(),
                    null,   // Inget rum på screening
                    movieDto
            );
        }

        return new AdminBookingResponseDto(
                booking.getId(),
                booking.getStatus(),
                booking.getReservationStartTime(), // alltid bokningens tider
                booking.getReservationEndTime(),   // alltid bokningens tider
                booking.getNumberOfGuests(),
                booking.getRoomEquipment(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd(),
                roomDto, // Kundens valda rum
                booking.getSpeakerName(),
                screeningDto      // Screening utan screeningens egna rum
        );
    }
}