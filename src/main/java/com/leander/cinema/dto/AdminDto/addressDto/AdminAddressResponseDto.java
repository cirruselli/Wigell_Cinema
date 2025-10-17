package com.leander.cinema.dto.AdminDto.addressDto;

public record AdminAddressResponseDto(
        Long addressId,
        String street,
        String postalCode,
        String city
) {
}
