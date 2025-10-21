package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseDto;
import com.leander.cinema.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class CustomerMapper {

    public static Customer toCustomerEntity(AdminCustomerWithAccountRequestDto body) {
        return new Customer(
                body.firstName().trim(),
                body.lastName().trim(),
                body.email().trim(),
                body.phone().trim());
    }

    public static void updateCustomer(Customer customer, AdminCustomerWithAccountRequestDto updatedCustomer) {
        customer.setFirstName(updatedCustomer.firstName().trim());
        customer.setLastName(updatedCustomer.lastName().trim());
        customer.setEmail(updatedCustomer.email().trim());
        customer.setPhone(updatedCustomer.phone().trim());
    }

    public static AdminCustomerResponseDto toAdminCustomerResponseDto(Customer customer) {

        // --- Adresser ---
        List<AdminAddressResponseDto> addressDtos = new ArrayList<>();
        for (Address address : customer.getAddresses()) {
            addressDtos.add(new AdminAddressResponseDto(
                    address.getId(),
                    address.getStreet(),
                    address.getPostalCode(),
                    address.getCity()
            ));
        }

        // --- Biljetter ---
        List<AdminTicketResponseDto> ticketDtos = new ArrayList<>();

        for (Ticket ticket : customer.getTickets()) {

            AdminScreeningResponseDto screeningDto = null;
            AdminBookingResponseDto bookingDto = null;

            // Om biljetten hör till en screening
            if (ticket.getScreening() != null) {
                screeningDto = ScreeningMapper.toAdminScreeningResponseDto(ticket.getScreening());
            }

            // Om biljetten hör till en bokning
            if (ticket.getBooking() != null) {
                bookingDto = BookingMapper.toAdminBookingResponseDto(ticket.getBooking());
            }

            // Dynamisk beräkning av pris per biljett och totalpris
            BigDecimal priceSek = calculateTicketPrice(ticket);
            BigDecimal totalPriceSek = priceSek.multiply(BigDecimal.valueOf(ticket.getNumberOfTickets()));
            BigDecimal factor = new BigDecimal("0.11");
            BigDecimal priceUsd = priceSek.multiply(factor);
            BigDecimal totalPriceUsd = totalPriceSek.multiply(factor);


            ticketDtos.add(new AdminTicketResponseDto(
                    ticket.getId(),
                    ticket.getNumberOfTickets(),
                    ticket.getCustomer().getFirstName(),
                    ticket.getCustomer().getLastName(),
                    priceSek,
                    priceUsd,
                    totalPriceSek,
                    totalPriceUsd,
                    screeningDto,
                    bookingDto
            ));
        }

        // --- Bokningar ---
        List<AdminBookingResponseDto> bookingDtos = new ArrayList<>();
        for (Booking booking : customer.getBookings()) {
            bookingDtos.add(BookingMapper.toAdminBookingResponseDto(booking));
        }

        return new AdminCustomerResponseDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                addressDtos,
                ticketDtos,
                bookingDtos,
                customer.getAppUser().getUsername()
        );
    }

    //Hjälpmetod för att beräkna biljettpris
    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();
            if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                return booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
            }
            if (booking.getMovie() != null) {
                BigDecimal roomPricePerGuest = booking.getRoom().getPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);
                return roomPricePerGuest;
            }
        }
        if (ticket.getScreening() != null) {
            Screening screening = ticket.getScreening();
            BigDecimal roomPricePerGuest = screening.getPriceSek();
            return roomPricePerGuest;
        }
        return BigDecimal.ZERO;
    }

}
