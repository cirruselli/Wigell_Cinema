package com.leander.cinema.controller;

import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.service.CustomerService;
import com.leander.cinema.service.MovieService;
import com.leander.cinema.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminController {
    private final CustomerService customerService;
    private final MovieService movieService;
    private final RoomService roomService;

    public AdminController(CustomerService customerService,
                           MovieService movieService,
                           RoomService roomService) {
        this.customerService = customerService;
        this.movieService = movieService;
        this.roomService = roomService;
    }

    // --- KUNDER ---

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponseDto>> customers() {
        List<AdminCustomerResponseDto> response = customerService.getAllCustomers();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/customers")
    public ResponseEntity<AdminCustomerResponseDto> customer(@Valid @RequestBody AdminCustomerRequestDto body) {
        AdminCustomerResponseDto response = customerService.createCustomer(body);
        URI location = URI.create("/customers/" + response.customerId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<AdminCustomerResponseDto> customer(@PathVariable Long customerId, @Valid @RequestBody AdminCustomerRequestDto body) {
        AdminCustomerResponseDto response = customerService.updateCustomer(customerId, body);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Void> customer(@PathVariable Long customerId) {
        if (customerService.deleteCustomer(customerId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- FILMER ---

    @PostMapping("/movies")
    public ResponseEntity<AdminMovieResponseDto> movie(@Valid @RequestBody AdminMovieRequestDto body) {
        AdminMovieResponseDto response = movieService.createMovie(body);
        URI location = URI.create("/movies/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> movie(@PathVariable Long movieId) {
        if (movieService.deleteMovie(movieId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- RUM ---

    @GetMapping("/rooms")
    public ResponseEntity<List<AdminRoomResponseDto>> rooms() {
        List<AdminRoomResponseDto> response = roomService.getAllRooms();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<AdminRoomResponseDto> room(@PathVariable Long roomId) {
        return ResponseEntity.ok().body(roomService.getRoomById(roomId));
    }
}
