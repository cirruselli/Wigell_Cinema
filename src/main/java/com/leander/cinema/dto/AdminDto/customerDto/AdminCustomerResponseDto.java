package com.leander.cinema.dto.AdminDto.customerDto;

public record AdminCustomerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
        //Ska Customer-response i GET CUSTOMERS innehålla ALL info som customer innehåller
        // alltså även information från relationerna? så som address, biljetter och bookings?
) {
}
