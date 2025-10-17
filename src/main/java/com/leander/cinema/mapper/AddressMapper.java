package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.entity.Address;

public class AddressMapper {

    public static AdminAddressResponseDto toAdminAddressResponseDto(Address address) {
        return new AdminAddressResponseDto(
                address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }
}
