package com.leander.cinema.controller;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieRequestDto;
import com.leander.cinema.dto.AdminDto.movieDto.AdminMovieResponseDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomRequestDto;
import com.leander.cinema.dto.AdminDto.roomDto.AdminRoomResponseDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.dto.CustomerDto.movieDto.MovieResponseDto;
import com.leander.cinema.dto.CustomerDto.screeningDto.ScreeningResponseDto;
import com.leander.cinema.service.CustomerService;
import com.leander.cinema.service.MovieService;
import com.leander.cinema.service.RoomService;
import com.leander.cinema.service.ScreeningService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

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

    // === HANTERING AV KUNDER ===

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponseDto>> customers() {
        List<AdminCustomerResponseDto> response = customerService.getAllCustomers();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/customers")
    public ResponseEntity<AdminCustomerResponseDto> customer(@Valid @RequestBody AdminCustomerWithAccountRequestDto body) {
        AdminCustomerResponseDto response = customerService.createCustomer(body);
        logger.info("POST /api/v1/customers admin skapade kunden {}", response.customerId());
        URI location = URI.create("/api/v1/customers/" + response.customerId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<AdminCustomerResponseDto> customer(@PathVariable Long customerId, @Valid @RequestBody AdminCustomerWithAccountRequestDto body) {
        AdminCustomerResponseDto response = customerService.updateCustomer(customerId, body);
        logger.info("PUT /api/v1/customers/{customerId} admin uppdaterade kunden {}", customerId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Void> customer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        logger.info("DELETE /api/v1/customers/{customerId} admin tog bort kund {}", customerId);
        return ResponseEntity.noContent().build();
    }

    // === ADRESSER ===

    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<AdminAddressResponseDto> address(@PathVariable Long customerId, @Valid @RequestBody AdminAddressRequestDto body) {
        AdminAddressResponseDto response = customerService.addAddressToCustomer(customerId, body);
        logger.info("POST /api/v1/customers/{customerId}/addresses admin skapade adress {} på kund {}", response.addressId(), customerId);
        URI location = URI.create("/api/v1/customers/" + customerId + "/addresses/" + response.addressId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> address(@PathVariable Long customerId, @PathVariable Long addressId) {
        customerService.removeAddressFromCustomer(customerId, addressId);
        logger.info("DELETE /api/v1/customers/{customerId}/addresses/{addressId} admin tog bort adress {} på kund {}", addressId, customerId);
        return ResponseEntity.noContent().build();
    }


    // === FILMER ===

    @GetMapping("/movies")
    public ResponseEntity<?> movies(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<AdminMovieResponseDto> adminMovies = movieService.getAllMoviesForAdmin();
            return ResponseEntity.ok(adminMovies);
        } else {
            List<MovieResponseDto> customerMovies = movieService.getAllMoviesForCustomer();
            return ResponseEntity.ok(customerMovies);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<AdminMovieResponseDto> movie(@PathVariable Long movieId) {
        return ResponseEntity.ok().body(movieService.getMovieById(movieId));
    }

    @PostMapping("/movies")
    public ResponseEntity<AdminMovieResponseDto> movie(@Valid @RequestBody AdminMovieRequestDto body) {
        AdminMovieResponseDto response = movieService.createMovie(body);
        logger.info("POST /api/v1/movies admin skapade filmen {}", response.movieId());
        URI location = URI.create("/api/v1/movies/" + response.movieId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> movie(@PathVariable Long movieId, @RequestParam(required = false) String ignore) {
        movieService.deleteMovie(movieId);
        logger.info("DELETE /api/v1/movies admin tog bort film {}", movieId);
        return ResponseEntity.noContent().build();
    }

    // === RUM ===

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
        logger.info("POST /api/v1/rooms admin skapade lokal {}", response.toString());
        URI location = URI.create("api/v1/rooms/" + response.roomId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<AdminRoomResponseDto> room(@PathVariable Long roomId, @Valid @RequestBody AdminRoomRequestDto body) {
        AdminRoomResponseDto response = roomService.updateRoom(roomId, body);
        logger.info("PUT /api/v1/rooms/{roomId} admin uppdaterade lokal {}", roomId);
        return ResponseEntity.ok().body(response);
    }

    // === FÖRESTÄLLNINGAR ===

    @GetMapping("/screenings")
    public ResponseEntity<List<?>> screenings(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        boolean isAdmin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            List<AdminScreeningResponseDto> response = screeningService.getAllScreeningsForAdmin();
            return ResponseEntity.ok(response);
        } else {
            if (movieId == null || date == null) {
                return ResponseEntity.badRequest()
                        .body(List.of("MovieId och date krävs för kunder"));
            }
            List<ScreeningResponseDto> response = screeningService.getScreeningsByMovieAndDate(movieId, date);
            return ResponseEntity.ok(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/screenings")
    public ResponseEntity<AdminScreeningResponseDto> screening(@Valid @RequestBody AdminScreeningRequestDto body) {
        AdminScreeningResponseDto response = screeningService.createScreening(body);
        logger.info("POST /api/v1/screenings admin skapade föreställningen {}", response.screeningId());
        URI location = URI.create("/api/v1/screenings/" + response.screeningId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/screenings/{screeningId}")
    public ResponseEntity<Void> screening(@PathVariable Long screeningId) {
        screeningService.deleteScreening(screeningId);
        logger.info("DELETE /api/v1/screenings admin tog bort film {}", screeningId);
        return ResponseEntity.noContent().build();
    }
}