package com.leander.cinema.repository;

import com.leander.cinema.entity.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByStreetAndPostalCodeAndCity(String street, String postalCode, String city);
}
