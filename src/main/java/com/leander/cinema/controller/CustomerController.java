package com.leander.cinema.controller;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPatchRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.dto.CustomerDto.ticketDto.TicketResponseDto;
import com.leander.cinema.service.BookingService;
import com.leander.cinema.service.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final BookingService bookingService;
    private final TicketService ticketService;

    public CustomerController(BookingService bookingService,
                              TicketService ticketService) {
        this.bookingService = bookingService;
        this.ticketService = ticketService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getBookings(@RequestParam Long customerId) {
        List<BookingResponseDto> response = bookingService.getActiveAndCompletedBookings(customerId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> booking(@Valid @RequestBody BookingPostRequestDto body) {
        BookingResponseDto response = bookingService.createBooking(body);
        logger.info("POST /api/v1/bookings/ kunden reserverade lokal {}", response.toString());
        URI location = URI.create("/api/v1/bookings" + response.bookingId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponseDto> booking(@PathVariable Long bookingId, @Valid @RequestBody BookingPatchRequestDto body) {
        BookingResponseDto response = bookingService.updateBooking(bookingId, body);
        logger.info("PATCH /api/v1/bookings/{bookingId} kunden uppdaterade bokning {} {}",bookingId, response.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDto> ticket(@Valid @RequestBody TicketRequestDto body) {
        TicketResponseDto response = ticketService.buyTicket(body);
        logger.info("POST /api/v1/tickets kunden köpte biljett {}", response.toString());
        URI location = URI.create("/api/v1/tickets" + response.ticketId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponseDto>> tickets(@RequestParam Long customerId) {
        List<TicketResponseDto> response = ticketService.getTicketsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}
