package com.leander.cinema.controller;

import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountCreateDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomRequestDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.service.CustomerService;
import com.leander.cinema.service.MovieService;
import com.leander.cinema.service.RoomService;
import com.leander.cinema.service.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminController {
    private final CustomerService customerService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final ScreeningService screeningService;

    public AdminController(CustomerService customerService,
                           MovieService movieService,
                           RoomService roomService,
                           ScreeningService screeningService) {
        this.customerService = customerService;
        this.movieService = movieService;
        this.roomService = roomService;
        this.screeningService = screeningService;
    }

    // --- KUNDER ---

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponseDto>> customers() {
        List<AdminCustomerResponseDto> response = customerService.getAllCustomers();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/customers")
    public ResponseEntity<AdminCustomerResponseDto> customer(@Valid @RequestBody AdminCustomerWithAccountCreateDto body) {
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

//    @GetMapping("/movies")
//    public List<?> movies(Authentication auth) {
//        List<AdminMovieResponseDto> response = movieService.getAllMoviesForAdmin();
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<AdminMovieResponseDto> movie(@PathVariable Long movieId) {
        return ResponseEntity.ok().body(movieService.getMovieById(movieId));
    }

    @PostMapping("/movies")
    public ResponseEntity<AdminMovieResponseDto> movie(@Valid @RequestBody AdminMovieRequestDto body) {
        AdminMovieResponseDto response = movieService.createMovie(body);
        URI location = URI.create("/movies/" + response.movieId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> movie(@PathVariable Long movieId, @RequestParam(required = false) String ignore) {
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

    @PostMapping("/rooms")
    public ResponseEntity<AdminRoomResponseDto> room(@Valid @RequestBody AdminRoomRequestDto body) {
        AdminRoomResponseDto response = roomService.createRoom(body);
        URI location = URI.create("/rooms/" + response.roomId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<AdminRoomResponseDto> room(@PathVariable Long roomId, @Valid @RequestBody AdminRoomRequestDto body) {
        AdminRoomResponseDto response = roomService.updateRoom(roomId, body);
        return ResponseEntity.ok().body(response);
    }

    // --- FÖRESTÄLLNINGAR ---

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/screenings")
    public ResponseEntity<List<AdminScreeningResponseDto>> screenings() {
        List<AdminScreeningResponseDto> response = screeningService.getAllScreenings();
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/screenings")
    public ResponseEntity<AdminScreeningResponseDto> screening(@Valid @RequestBody AdminScreeningRequestDto body) {
        AdminScreeningResponseDto response = screeningService.createScreening(body);
        URI location = URI.create("/screenings/" + response.screeningId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/screenings/{screeningId}")
    public ResponseEntity<Void> screening(@PathVariable Long screeningId) {
        if (screeningService.deleteScreening(screeningId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
