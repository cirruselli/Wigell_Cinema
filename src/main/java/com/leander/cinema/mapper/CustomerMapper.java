package com.leander.cinema.mapper;

import com.leander.cinema.currency.CurrencyCalculator;
import com.leander.cinema.currency.CurrencyConverter;
import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseContent;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketBookingResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseContent;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketScreeningResponseDto;
import com.leander.cinema.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CustomerMapper {

    private static final CurrencyConverter currencyConverter = new CurrencyConverter();

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
        List<AdminTicketResponseContent> ticketDtos = new ArrayList<>();

        for (Ticket ticket : customer.getTickets()) {

            // Kontrollera att biljetten har antingen screening eller booking, inte båda
            if (ticket.getScreening() != null && ticket.getBooking() != null) {
                throw new IllegalStateException("Ticket " + ticket.getId() + " har både screening och booking - endast en av de får sättas!");
            }

            // Dynamisk beräkning av pris per biljett och totalpris
            BigDecimal priceSek = CurrencyCalculator.calculateTicketPrice(ticket);
            BigDecimal totalPriceSek = priceSek.multiply(BigDecimal.valueOf(ticket.getNumberOfTickets()));
            BigDecimal priceUsd = currencyConverter.toUsd(priceSek);
            BigDecimal totalPriceUsd = currencyConverter.toUsd(totalPriceSek);

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
                        BookingMapper.toAdminBookingResponseContent(ticket.getBooking())
                ));
            }
        }

        // --- Bokningar ---
        List<AdminBookingResponseContent> bookingDtos = new ArrayList<>();
        for (Booking booking : customer.getBookings()) {
            bookingDtos.add(BookingMapper.toAdminBookingResponseContent(booking));
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
}
