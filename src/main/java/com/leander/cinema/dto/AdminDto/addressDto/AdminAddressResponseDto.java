package com.leander.cinema.dto.AdminDto.addressDto;

public record AdminAddressResponseDto(
        Long id,
        String street,
        String postalCode,
        String city
) {
}
