package com.leander.cinema.controller;

import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminController {
    private final CustomerService customerService;

    public AdminController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponseDto>> customers() {
        List<AdminCustomerResponseDto> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers")
    public ResponseEntity<AdminCustomerResponseDto> customer(@Valid @RequestBody AdminCustomerRequestDto body) {
        AdminCustomerResponseDto response =  customerService.createCustomer(body);
        URI location = URI.create("/customers" + response.customerId());
        return ResponseEntity.created(location).body(response);

    }
}
