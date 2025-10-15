package com.leander.cinema.mapper;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;

public class BookingMapper {
    public static Booking toBookingEntity(BookingPostRequestDto body) {
        Booking booking = new Booking();
        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());
        booking.setNumberOfGuests(body.numberOfGuests());
        return booking;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        //Kontrollerar att inga null-värden sätts
        // Room
        String roomName = "----";
        int maxGuests = 0;
        if (booking.getRoom() != null) {
            if (booking.getRoom().getName() != null) {
                roomName = booking.getRoom().getName();
            }
            maxGuests = booking.getRoom().getMaxGuests();
        }

        // Speaker
        String speakerName = "----";
        if (booking.getScreening() != null && booking.getScreening().getSpeaker() != null) {
            if (booking.getScreening().getSpeaker().getName() != null) {
                speakerName = booking.getScreening().getSpeaker().getName();
            }
        }

        // Movie
        String movieTitle = "----";
        if (booking.getScreening() != null && booking.getScreening().getMovie() != null) {
            if (booking.getScreening().getMovie().getTitle() != null) {
                movieTitle = booking.getScreening().getMovie().getTitle();
            }
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
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                roomName,
                maxGuests,
                speakerName,
                movieTitle,
                customerFirstName,
                customerLastName,
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd()
        );
    }
}