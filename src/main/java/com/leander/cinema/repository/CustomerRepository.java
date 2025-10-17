package com.leander.cinema.repository;

import com.leander.cinema.entity.Customer;
import com.leander.cinema.security.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);

    Optional<Customer> findByAppUser(AppUser appUser);
}
