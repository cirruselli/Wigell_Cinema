package com.leander.cinema.controller;

import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingResponseDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.service.BookingService;
import com.leander.cinema.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    private final MovieService movieService;
    private final BookingService bookingService;

    public CustomerController(MovieService movieService, BookingService bookingService) {
        this.movieService = movieService;
        this.bookingService = bookingService;
    }
//
//    @GetMapping("/movies")
//    public ResponseEntity<List<MovieResponseDto>> movies() {
//        List<MovieResponseDto> response = movieService.getAllMoviesForCustomer();
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/bookings")
//    public ResponseEntity<BookingResponseDto> booking(@Valid @RequestBody BookingPostRequestDto requestDto) {
//        BookingResponseDto response = bookingService.createBooking(requestDto);
//        URI location = URI.create("/api/v1/bookings" + response.id());
//        return ResponseEntity.created(location).body(response);
//    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponseDto> booking(@PathVariable Long bookingId, @Valid @RequestBody BookingPatchRequestDto body) {
        BookingResponseDto response = bookingService.updateBooking(bookingId, body);
        return ResponseEntity.ok(response);
    }


    // Lista föreställiningar ska ha    @PreAuthorize("hasRole('USER')")  för att undivka att admin kommer åt endpointen
}
