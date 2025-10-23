package com.leander.cinema.dto.AdminDto.customerDto;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketResponse;

import java.util.List;

public record AdminCustomerResponseDto(
        Long customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        List<AdminAddressResponseDto> addresses,
        List<AdminTicketResponse> tickets,
        List<AdminBookingResponseDto> bookings,
        String username
) {
}
