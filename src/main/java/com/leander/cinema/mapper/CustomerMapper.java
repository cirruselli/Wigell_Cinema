package com.leander.cinema.mapper;

import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.entity.Customer;

public class CustomerMapper {

    public static AdminCustomerResponseDto toAdminCustomerResponseDto(Customer customer) {
        return new AdminCustomerResponseDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone());
    }
}
