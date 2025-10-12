package com.leander.cinema.mapper;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingMapper {
    public static Booking toBookingEntity(BookingPostRequestDto body) {
        Booking booking = new Booking();
        booking.setReservationTime(body.reservationTime());
        booking.setNumberOfGuests(body.numberOfGuests());
        booking.setEquipment(body.equipment());
        return booking;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        BookingResponseDto response = new BookingResponseDto(
                booking.getId(),
                booking.getReservationTime(),
                booking.getNumberOfGuests(),
                booking.getEquipment(),
                booking.getRoom().getName(),
                booking.getRoom().getMaxGuests(),
                booking.getScreening().getSpeakerName(),
                booking.getScreening().getMovie().getTitle(),
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),

                );
        BigDecimal totalPriceSek,
        BigDecimal totalPriceUsd
    }
}