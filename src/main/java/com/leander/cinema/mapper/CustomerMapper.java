package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Address;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.entity.Ticket;

import java.util.ArrayList;
import java.util.List;


public class CustomerMapper {

    public static Customer toCustomerEntity (AdminCustomerRequestDto body){
        return new Customer(body.firstName(), body.lastName(), body.email(), body.phone());
    }

    public static AdminCustomerResponseDto toAdminCustomerResponseDto(Customer customer) {
        List<AdminAddressResponseDto> addressDtos = new ArrayList<>();
        for(Address address : customer.getAddresses()) {
            addressDtos.add(new AdminAddressResponseDto(
                    address.getId(),
                    address.getStreet(),
                    address.getPostalCode(),
                    address.getCity()
            ));
        }

        List<AdminTicketResponseDto> ticketDtos = new ArrayList<>();
        for(Ticket ticket : customer.getTickets()) {
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

        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for(Booking booking : customer.getBookings()) {
            bookingDtos.add(new BookingResponseDto(
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
                    booking.getTotalPriceUsd()
            ));
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
