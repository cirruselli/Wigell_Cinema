package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.entity.Address;
import com.leander.cinema.entity.Customer;
import com.leander.cinema.mapper.CustomerMapper;
import com.leander.cinema.repository.AddressRepository;
import com.leander.cinema.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
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

    public AdminCustomerResponseDto createCustomer(AdminCustomerRequestDto body) {
        Customer customer = CustomerMapper.toCustomerEntity(body);

        List<Address> addresses = new ArrayList<>();

        for (AdminAddressRequestDto addressDto : body.addresses()) {
            Address existingAddress = addressRepository
                    .findByStreetAndPostalCodeAndCity(
                            addressDto.street(),
                            addressDto.postalCode(),
                            addressDto.city()
                    );

            if (existingAddress != null) {
                /*Kontrollera om kunden har adressen ->
                 om inte så sätt till existerande adress från databasen
               */
                if (!addresses.contains(existingAddress)) {
                    addresses.add(existingAddress);
                }
            } else {
                Address newAddress = new Address();
                newAddress.setStreet(addressDto.street());
                newAddress.setPostalCode(addressDto.postalCode());
                newAddress.setCity(addressDto.city());
                addressRepository.save(newAddress);
                addresses.add(newAddress);
            }
        }
        customer.setAddresses(addresses);

        /* Sätt tomma listor för biljetter och bokningar
        så att det ändå skapats upp listor för dessa i POST-requesten
         */
        customer.setTickets(new ArrayList<>());
        customer.setBookings(new ArrayList<>());

        customerRepository.save(customer);
        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }
}
