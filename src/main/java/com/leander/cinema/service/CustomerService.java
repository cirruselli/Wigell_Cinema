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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Transactional
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

    @Transactional
    public boolean deleteCustomer(long id) {
        Optional<Customer> customer = customerRepository.findById(id);

        if (customer.isPresent()) {
            Customer customerEntity = customer.get();
            // Tar bort kopplingarna mellan kunden och adressen i mellantabellen (customer_addresses)
            customerEntity.getAddresses().clear();
            // Tar bort kunden från tabellen customers
            customerRepository.delete(customerEntity);

            // Skickar delete av relationerna till DB innan rensning av adresserna i adress-tabellen
            customerRepository.flush();

            List<Address> allAddresses = addressRepository.findAll();
            for (Address address : allAddresses) {
                // Tar bort alla adresser som saknar kopplade kunder
                if(address.getCustomers() == null || address.getCustomers().isEmpty()) {
                    addressRepository.delete(address);
                }
            }
            return true;
        }
        else{
            return false;
        }
    }
}
