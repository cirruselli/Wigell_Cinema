package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.mapper.CustomerMapper;
import com.leander.cinema.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<AdminCustomerResponseDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        List<AdminCustomerResponseDto> responseList = new ArrayList<>();
        for (Customer customer : customers) {
            AdminCustomerResponseDto customerResponse = CustomerMapper.toAdminCustomerResponseDto(customer);
            responseList.add(customerResponse);
        }
        return responseList;
    }
}
