package com.leander.cinema.dto.AdminDto.customerDto;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponseDto;

import java.util.List;

public record AdminCustomerResponseDto(
        Long customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        List<AdminAddressResponseDto> addresses,
        List<AdminTicketResponseDto> tickets,
        List<AdminBookingResponseDto> bookings,
        String username
) {
}
