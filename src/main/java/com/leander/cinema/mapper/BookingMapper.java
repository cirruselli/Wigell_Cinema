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
        booking.setEquipment(body.equipment());
        return booking;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                booking.getEquipment(),
                booking.getRoom().getName(),
                booking.getRoom().getMaxGuests(),
                booking.getScreening().getSpeakerName(),
                booking.getScreening().getMovie().getTitle(),
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd());
    }
}