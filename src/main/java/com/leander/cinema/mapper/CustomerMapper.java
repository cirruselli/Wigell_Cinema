package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountCreateDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Address;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.entity.Ticket;

import java.util.ArrayList;
import java.util.List;


public class CustomerMapper {

    public static Customer toCustomerEntity(AdminCustomerWithAccountCreateDto body) {
        return new Customer(
                body.firstName().trim(),
                body.lastName().trim(),
                body.email().trim(),
                body.phone().trim());
    }

    public static void updateCustomer(Customer customer, AdminCustomerRequestDto updatedCustomer) {
        customer.setFirstName(updatedCustomer.firstName().trim());
        customer.setLastName(updatedCustomer.lastName().trim());
        customer.setEmail(updatedCustomer.email().trim());
        customer.setPhone(updatedCustomer.phone().trim());
    }

    public static AdminCustomerResponseDto toAdminCustomerResponseDto(Customer customer) {
        List<AdminAddressResponseDto> addressDtos = new ArrayList<>();
        for (Address address : customer.getAddresses()) {
            addressDtos.add(new AdminAddressResponseDto(
                    address.getId(),
                    address.getStreet(),
                    address.getPostalCode(),
                    address.getCity()
            ));
        }

        List<AdminTicketResponseDto> ticketDtos = new ArrayList<>();
        for (Ticket ticket : customer.getTickets()) {
            ticketDtos.add(new AdminTicketResponseDto(
                    ticket.getId(),
                    ticket.getNumberOfTickets(),
                    ticket.getCustomer().getFirstName(),
                    ticket.getCustomer().getLastName(),
                    ticket.getTotalPriceSek(),
                    ticket.getTotalPriceUsd(),
                    ticket.getScreening().getId()
            ));
        }

        List<AdminBookingResponseDto> bookingDtos = new ArrayList<>();
        for (Booking booking : customer.getBookings()) {

            Long speakerId = null;
            Long movieId = null;

            String speakerName = null;
            String movieTitle = null;

            int speakerDuration = 0;
            int movieDuration = 0;

            if (booking.getScreening() != null && booking.getScreening().getId() != null) {
                if (booking.getScreening().getSpeaker() != null) {
                    speakerId = booking.getScreening().getSpeaker().getId();
                    speakerName = booking.getScreening().getSpeaker().getName();
                    speakerDuration = booking.getScreening().getSpeaker().getDuration();
                } else {
                    speakerId = null;
                    speakerName = "----";
                    speakerDuration = 0;
                }

                if (booking.getScreening().getMovie() != null) {
                    movieId = booking.getScreening().getMovie().getId();
                    movieTitle = booking.getScreening().getMovie().getTitle();
                    movieDuration = booking.getScreening().getMovie().getDuration();
                } else {
                    movieId = null;
                    movieTitle = "----";
                    movieDuration = 0;
                }
            }

            bookingDtos.add(new AdminBookingResponseDto(
                    booking.getId(),
                    booking.getReservationStartTime(),
                    booking.getReservationEndTime(),
                    booking.getNumberOfGuests(),
                    booking.getRoom().getId(),
                    booking.getRoom().getName(),
                    booking.getRoom().getMaxGuests(),
                    booking.getRoom().getStandardEquipment(),
                    speakerId,
                    speakerName,
                    speakerDuration,
                    movieId,
                    movieTitle,
                    movieDuration,
                    booking.getTotalPriceSek(),
                    booking.getTotalPriceUsd()));
        }

        return new AdminCustomerResponseDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                addressDtos,
                ticketDtos,
                bookingDtos
        );
    }
}
