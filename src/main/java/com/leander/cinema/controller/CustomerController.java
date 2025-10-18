package com.leander.cinema.controller;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketResponseDto;
import com.leander.cinema.service.BookingService;
import com.leander.cinema.service.MovieService;
import com.leander.cinema.service.ScreeningService;
import com.leander.cinema.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    private final MovieService movieService;
    private final BookingService bookingService;
    private final TicketService ticketService;
    private final ScreeningService screeningService;

    public CustomerController(MovieService movieService,
                              BookingService bookingService,
                              TicketService ticketService,
                              ScreeningService screeningService) {
        this.movieService = movieService;
        this.bookingService = bookingService;
        this.ticketService = ticketService;
        this.screeningService = screeningService;
    }
//
//    @GetMapping("/movies")
//    public ResponseEntity<List<MovieResponseDto>> movies() {
//        List<MovieResponseDto> response = movieService.getAllMoviesForCustomer();
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> booking(@Valid @RequestBody BookingPostRequestDto body) {
        BookingResponseDto response = bookingService.createBooking(body);
        URI location = URI.create("/api/v1/bookings" + response.bookingId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponseDto> booking(@PathVariable Long bookingId, @Valid @RequestBody BookingPatchRequestDto body) {
        BookingResponseDto response = bookingService.updateBooking(bookingId, body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDto> ticket(@Valid @RequestBody TicketRequestDto body) {
        TicketResponseDto response = ticketService.buyTicket(body);
        URI location = URI.create("/api/v1/tickets" + response.ticketId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> tickets(@RequestParam Long customerId) {
        List<TicketResponseDto> response = ticketService.getTicketsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}
