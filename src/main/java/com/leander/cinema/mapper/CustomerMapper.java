package com.leander.cinema.mapper;

import com.leander.cinema.currencyConverter.CurrencyConverter;
import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketBookingResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponse;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketScreeningResponseDto;
import com.leander.cinema.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class CustomerMapper {

    static CurrencyConverter currencyConverter = new CurrencyConverter();

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
        List<AdminTicketResponse> ticketDtos = new ArrayList<>();

        for (Ticket ticket : customer.getTickets()) {

            // Kontrollera att biljetten har antingen screening eller booking, inte båda
            if (ticket.getScreening() != null && ticket.getBooking() != null) {
                throw new IllegalStateException("Ticket " + ticket.getId() + " har både screening och booking - endast en av de får sättas!");
            }

            // Dynamisk beräkning av pris per biljett och totalpris
            BigDecimal priceSek = calculateTicketPrice(ticket);
            BigDecimal totalPriceSek = priceSek.multiply(BigDecimal.valueOf(ticket.getNumberOfTickets()));
            BigDecimal priceUsd = currencyConverter.toUSD(totalPriceSek);
            BigDecimal totalPriceUsd = currencyConverter.toUSD(totalPriceSek);

            // Skapa rätt DTO beroende på typ
            if (ticket.getScreening() != null) {
                ticketDtos.add(new AdminTicketScreeningResponseDto(
                        ticket.getId(),
                        ticket.getNumberOfTickets(),
                        priceSek,
                        priceUsd,
                        totalPriceSek,
                        totalPriceUsd,
                        ScreeningMapper.toAdminScreeningResponseDto(ticket.getScreening())
                ));
            } else if (ticket.getBooking() != null) {
                ticketDtos.add(new AdminTicketBookingResponseDto(
                        ticket.getId(),
                        ticket.getNumberOfTickets(),
                        priceSek,
                        priceUsd,
                        totalPriceSek,
                        totalPriceUsd,
                        BookingMapper.toAdminBookingResponseDto(ticket.getBooking())
                ));
            }
        }

        // --- Bokningar ---
        List<AdminBookingResponseDto> bookingDtos = new ArrayList<>();
        for (Booking booking : customer.getBookings()) {
            bookingDtos.add(BookingMapper.toAdminBookingResponseDto(booking));
        }

        String username = null;
        if (customer.getAppUser() != null) {
            username = customer.getAppUser().getUsername();
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
                username
        );

    }

    //Hjälpmetod för att beräkna biljettpris
    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();
            if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                return booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()).add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP);
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
