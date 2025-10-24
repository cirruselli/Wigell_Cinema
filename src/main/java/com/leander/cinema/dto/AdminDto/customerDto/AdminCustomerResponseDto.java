package com.leander.cinema.dto.AdminDto.customerDto;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseContent;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseContent;

import java.util.List;

public record AdminCustomerResponseDto(
        Long customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        List<AdminAddressResponseDto> addresses,
        List<AdminTicketResponseContent> tickets, // interface
        List<AdminBookingResponseContent> bookings, // interface
        String username
) { }
