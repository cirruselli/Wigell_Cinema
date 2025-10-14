package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;

public class BookingMapper {
    public static Booking toBookingEntity(BookingPostRequestDto body) {
        Booking booking = new Booking();
        booking.setReservationStartTime(body.reservationStartTime());
        booking.setReservationEndTime(body.reservationEndTime());
        booking.setNumberOfGuests(body.numberOfGuests());
        booking.getScreening().getRoom().setStandardEquipment(body.equipment());
        return booking;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                booking.getRoom().getName(),
                booking.getRoom().getMaxGuests(),
                booking.getRoom().getStandardEquipment(),
                booking.getScreening().getSpeaker().getName(),
                booking.getScreening().getMovie().getTitle(),
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd());
    }

    public static AdminBookingResponseDto toAdminBookingResponseDto(Booking booking) {
        return new AdminBookingResponseDto(
                booking.getId(),
                booking.getReservationStartTime(),
                booking.getReservationEndTime(),
                booking.getNumberOfGuests(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getRoom().getMaxGuests(),
                booking.getRoom().getStandardEquipment(),
                booking.getScreening().getSpeaker().getId(),
                booking.getScreening().getSpeaker().getName(),
                booking.getScreening().getSpeaker().getDuration(),
                booking.getScreening().getMovie().getId(),
                booking.getScreening().getMovie().getTitle(),
                booking.getScreening().getMovie().getDuration(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceUsd());
    }

}