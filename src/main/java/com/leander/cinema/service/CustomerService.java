package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingUpdateRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketUpdateRequestDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.*;
import com.leander.cinema.mapper.AddressMapper;
import com.leander.cinema.mapper.CustomerMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import com.leander.cinema.security.Role;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerService {
    Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,
                           AddressRepository addressRepository,
                           TicketRepository ticketRepository,
                           ScreeningRepository screeningRepository,
                           BookingRepository bookingRepository,
                           MovieRepository movieRepository,
                           RoomRepository roomRepository,
                           AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // === Lista kunder ===
    @Transactional(readOnly = true)
    public List<AdminCustomerResponseDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        List<AdminCustomerResponseDto> responseList = new ArrayList<>();
        for (Customer customer : customers) {
            AdminCustomerResponseDto customerResponse = CustomerMapper.toAdminCustomerResponseDto(customer);
            responseList.add(customerResponse);
        }
        return responseList;
    }

    // === Lägga till kund ===
    @Transactional
    public AdminCustomerResponseDto createCustomer(AdminCustomerWithAccountRequestDto newUser) {

        String email = newUser.email().trim();
        String username = newUser.username().trim();

        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email finns redan");
        }

        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Användarnamn finns redan");
        }

        Customer customer = CustomerMapper.toCustomerEntity(newUser);

        List<Address> addresses = new ArrayList<>();
        for (AdminAddressRequestDto addressDto : newUser.addresses()) {

            String street = addressDto.street().trim();
            String postalCode = addressDto.postalCode().trim();
            String city = addressDto.city().trim();

            Address existingAddress = addressRepository
                    .findByStreetAndPostalCodeAndCity(street, postalCode, city);

            if (existingAddress != null) {
                /*Kontrollera om kunden har adressen ->
                 om inte så sätt till existerande adress från databasen
               */
                if (!addresses.contains(existingAddress)) {
                    addresses.add(existingAddress);
                }
            } else {
                Address newAddress = new Address();
                newAddress.setStreet(addressDto.street().trim());
                newAddress.setPostalCode(addressDto.postalCode().trim());
                newAddress.setCity(addressDto.city().trim());
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

        AppUser appUser = new AppUser(
                newUser.username().trim(),
                passwordEncoder.encode(newUser.password().trim()),
                Set.of(Role.USER),
                customer
        );

        customer.setAppUser(appUser);

        customerRepository.save(customer);

        logger.info("Admin skapade kund {}", customer.getId());

        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }


    // === Uppdatera kund ===
    @Transactional
    public AdminCustomerResponseDto updateCustomer(Long id, AdminCustomerWithAccountRequestDto requestDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + id + " hittades inte"));

        // --- Uppdatera kundens primitiva fält ---
        CustomerMapper.updateCustomer(customer, requestDto);


        // --- UPPDATERA ADRESSER ---

        // --- Spara undan gamla adresser innan uppdatering ---
        List<Address> oldAddresses = new ArrayList<>(customer.getAddresses());

        // --- Skapa lista med nya adresser ---
        List<Address> updatedAddresses = new ArrayList<>();
        for (AdminAddressRequestDto addressDto : requestDto.addresses()) {
            String street = addressDto.street().trim();
            String postalCode = addressDto.postalCode().trim();
            String city = addressDto.city().trim();

            Address existingAddress = addressRepository.findByStreetAndPostalCodeAndCity(street, postalCode, city);

            if (existingAddress != null) {
                if (!updatedAddresses.contains(existingAddress)) {
                    updatedAddresses.add(existingAddress);
                }
            } else {
                Address newAddress = new Address();
                newAddress.setStreet(street);
                newAddress.setPostalCode(postalCode);
                newAddress.setCity(city);
                addressRepository.save(newAddress);
                logger.info("Admin skapade adress {} vid uppdatering av kund {}", newAddress.getId(), customer.getId());
                updatedAddresses.add(newAddress);
            }
        }

        // --- Uppdatera kundens adresser ---
        customer.setAddresses(updatedAddresses);
        customerRepository.save(customer);
        logger.info("Admin uppdaterade {} adress/er på kund {}", customer.getAddresses().size(), customer.getId());
        customerRepository.flush();

        // --- Kolla om gamla adresser nu blivit orphans ---
        for (Address oldAddress : oldAddresses) {
            if (!oldAddress.getCustomers().isEmpty()) continue; // fortfarande kopplad till någon kund
            addressRepository.delete(oldAddress);
            logger.info("Admin tog bort adress {} vid uppdatering av kund {}", oldAddress.getId(), customer.getId());
        }


        // --- UPPDATERA BILJETTER ---


        if (requestDto.tickets() == null) {
            throw new InvalidTicketException("Biljett/er måste anges");
        } else {
            List<Ticket> updatedTickets = new ArrayList<>();
            for (AdminTicketUpdateRequestDto ticketDto : requestDto.tickets()) {

                Ticket ticket = ticketRepository.findById(ticketDto.ticketId())
                        .orElseThrow(() -> new EntityNotFoundException("Biljett med id " + ticketDto.ticketId() + " hittades inte"));

                //Kontrollera att biljetten tillhör kunden
                if (!ticket.getCustomer().getId().equals(customer.getId())) {
                    throw new CustomerOwnershipException("Biljett med id " + ticketDto.ticketId() + " tillhör inte kunden");
                }

                // --- Hantera Föreställning och bokning ---
                if (ticketDto.screeningId() == null || ticketDto.bookingId() == null) {
                    throw new IllegalArgumentException("Både filmföreställning och bokning måste anges på biljett med id " + ticket.getId());
                }

                Screening screening = screeningRepository.findById(ticketDto.screeningId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Föreställning med id " + ticketDto.screeningId() + " hittades inte för biljett med id " + ticketDto.ticketId()));
                ticket.setScreening(screening);

                Screening ticketScreening = ticket.getScreening();
                if (ticketScreening == null) {
                    throw new IllegalStateException("Biljetten " + ticket.getId() + " har ingen kopplad föreställning efter uppdatering");
                }

                Booking booking = bookingRepository.findById(ticketDto.bookingId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Bokning med id " + ticketDto.bookingId() + " hittades inte för biljett med id " + ticketDto.ticketId()));
                ticket.setBooking(booking);


                ticket.setNumberOfTickets(ticketDto.numberOfTickets());

                //Räknar bara om totalbeloppet då enhetspriset ska vara låst vid uppdatering!
                ticket.setTotalPriceSek(ticket.getPriceSek()
                        .multiply(BigDecimal.valueOf(ticket.getNumberOfTickets())));
                ticket.setTotalPriceUsd(ticket.getPriceUsd()
                        .multiply(BigDecimal.valueOf(ticket.getNumberOfTickets())));

                ticket.setCustomer(customer);
                ticketRepository.save(ticket);
                bookingRepository.save(ticket.getBooking());

                // Hindrar att samma Ticket-objekt läggs till i listan igen
                if (!updatedTickets.contains(ticket)) {
                    updatedTickets.add(ticket);
                }
            }

            customer.getTickets().clear();
            customer.getTickets().addAll(updatedTickets);
        }

        // --- UPPDATERA BOKNING ---

        if (requestDto.bookings() == null) {
            throw new InvalidBookingException("Bokning/ar måste anges");
        } else {
            List<Booking> updatedBookings = new ArrayList<>();
            for (AdminBookingUpdateRequestDto bookingDto : requestDto.bookings()) {

                Booking booking = bookingRepository.findById(bookingDto.bookingId())
                        .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + bookingDto.bookingId() + " hittades inte"));

                //Kontrollera att bokningen tillhör kunden
                if (!booking.getCustomer().getId().equals(customer.getId())) {
                    throw new CustomerOwnershipException(
                            "Bokning med id " + bookingDto.bookingId() + " tillhör inte kunden med id " + customer.getId());
                }

                // Uppdatera rum
                Room room = roomRepository.findById(bookingDto.roomId())
                        .orElseThrow(() -> new EntityNotFoundException("Lokal med id " + bookingDto.roomId() + " hittades inte för bokning med id " + bookingDto.bookingId()));
                booking.setRoom(room);

                // Kontrollera överlappning med pågående screenings
                List<Screening> screenings = screeningRepository.findByRoom(room);
                for (Screening screening : screenings) {
                    LocalDateTime totalEndTime = screening.getTotalEndTime(); // film + städning
                    if (screening.getStartTime().isBefore(bookingDto.reservationEndTime())
                            && totalEndTime.isAfter(bookingDto.reservationStartTime())) {
                        throw new BookingConflictException(
                                "Tiderna för bokningen överlappar med en föreställning i samma rum."
                        );
                    }
                }

                // Kontrollera överlappning med andra bokningar i samma rum
                List<Booking> otherBookings = bookingRepository.findByRoomId(room.getId());
                for (Booking otherBooking : otherBookings) {
                    if (!otherBooking.getId().equals(booking.getId())) { // exkludera sig själv
                        boolean overlap = bookingDto.reservationStartTime().isBefore(otherBooking.getReservationEndTime())
                                && bookingDto.reservationEndTime().isAfter(otherBooking.getReservationStartTime());
                        if (overlap) {
                            throw new BookingConflictException("Tiderna för bokningen överlappar med en annan bokning i samma rum.");
                        }
                    }
                }

                // Kontrollera att sluttid inte är före starttid
                if (bookingDto.reservationEndTime() != null && bookingDto.reservationStartTime() != null
                        && bookingDto.reservationEndTime().isBefore(bookingDto.reservationStartTime())) {
                    throw new IllegalArgumentException("Sluttiden för bokningen kan inte vara före starttiden.");
                }

                // Hantera film vs talare
                if (bookingDto.movieId() == null || (bookingDto.speakerName() == null || bookingDto.speakerName().isBlank())) {
                    throw new InvalidBookingException("Både film och talare måste anges på bokning " + bookingDto.bookingId());
                }

                Movie movie = movieRepository.findById(bookingDto.movieId())
                        .orElseThrow(() -> new EntityNotFoundException("Filmen med id " + bookingDto.movieId() + " hittades inte på bokningen med id" + bookingDto.bookingId()));

                booking.setMovie(movie);

                booking.setSpeakerName(bookingDto.speakerName());

                booking.setReservationStartTime(bookingDto.reservationStartTime());
                booking.setReservationEndTime(bookingDto.reservationEndTime());
                booking.setNumberOfGuests(bookingDto.numberOfGuests());

                // --- Kontrollera att antal gäster inte överstiger rummets kapacitet ---
                int maxGuests = booking.getRoom().getMaxGuests();
                if (bookingDto.numberOfGuests() > maxGuests) {
                    throw new IllegalArgumentException("Antalet gäster (" + bookingDto.numberOfGuests() +
                            ") överstiger rummets maxkapacitet (" + maxGuests + ").");
                }

                // --- Hantera roomEquipment ---
                if (bookingDto.roomEquipment() == null) {
                    booking.setRoomEquipment(new ArrayList<>(booking.getRoom().getStandardEquipment()));
                } else if (bookingDto.roomEquipment().isEmpty()) {
                    booking.setRoomEquipment(new ArrayList<>());
                } else {
                    booking.setRoomEquipment(new ArrayList<>(bookingDto.roomEquipment()));
                }

                // Hindrar att samma Booking-objekt läggs till i listan igen
                if (!updatedBookings.contains(booking)) {
                    updatedBookings.add(booking);
                }

                booking.setStatus(bookingDto.bookingStatus());
                booking.setCustomer(customer);

            }
            customer.getBookings().clear();
            customer.getBookings().addAll(updatedBookings);
        }

        // --- UPPDATERA APPUSER ---
        AppUser appUser = customer.getAppUser();
        if (appUser != null) {
            if (requestDto.username() != null && !requestDto.username().isBlank()) {
                appUser.setUsername(requestDto.username());
            }
            if (requestDto.password() != null && !requestDto.password().isBlank()) {
                appUser.setPassword(passwordEncoder.encode(requestDto.password()));
            }
        }

        customerRepository.save(customer);
        logger.info("Admin uppdaterade kund {}", customer.getId());

        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }


    @Transactional
    public void deleteCustomer(Long id) {
        Customer customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + id + " hittades inte"));

        // --- Säkerhetskontroll ---
        boolean hasActiveBookings = false;
        for (Booking booking : customerEntity.getBookings()) {
            if (booking.getStatus() == BookingStatus.ACTIVE) {
                hasActiveBookings = true;
                break;
            }
        }

        if (hasActiveBookings) {
            throw new IllegalStateException("Kunden har fortfarande aktiva bokningar och kan inte tas bort");
        }

        // --- Koppla loss adresser ---
        if (customerEntity.getAddresses() != null && !customerEntity.getAddresses().isEmpty()) {
            customerEntity.getAddresses().clear();
            customerRepository.save(customerEntity);
            logger.info("Alla adresser kopplade till kund {} har frikopplats", customerEntity.getId());
        }

        // --- Ta bort kunden (cascade tar bort biljetter & bokningar) ---
        customerRepository.delete(customerEntity);
        customerRepository.flush();
        logger.info("Kund {} raderades", customerEntity.getId());

        // --- Rensa överblivna adresser ---
        List<Address> allAddresses = addressRepository.findAll();
        for (Address address : allAddresses) {
            if (address.getCustomers() == null || address.getCustomers().isEmpty()) {
                addressRepository.delete(address);
                logger.info("Adress {} raderades eftersom den inte längre är kopplad till någon kund", address.getId());
            }
        }

        logger.info("Admin tog bort kund {}", customerEntity.getId());
    }


    // === Lägg till adress ===
    @Transactional
    public AdminAddressResponseDto addAddressToCustomer(Long customerId, AdminAddressRequestDto body) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Kunden hittades inte"));

        String street = body.street().trim();
        String postalCode = body.postalCode().trim();
        String city = body.city().trim();

        Address address = addressRepository.findByStreetAndPostalCodeAndCity(street, postalCode, city);

        if (address == null) {
            address = new Address(street, postalCode, city);
            addressRepository.save(address);
            logger.info("Admin lade till adress {} på kund {}", address.getId(), customerId);
        }

        if (customer.getAddresses().contains(address)) {
            throw new AddressAlreadyExistsException("Kunden har redan denna adress");
        }

        customer.getAddresses().add(address);
        customerRepository.save(customer);

        logger.info("Admin uppdaterade användaren {}", customer.getId());

        return AddressMapper.toAdminAddressResponseDto(address);
    }

    // === Ta bort adress ===
    @Transactional
    public void removeAddressFromCustomer(Long customerId, Long addressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kunden med id " + customerId + " hittades inte"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Adressen med id " + addressId + " hittades inte"));

        // Kolla att kunden faktiskt har adressen
        if (!customer.getAddresses().contains(address)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Kunden har inte denna adress."
            );
        }

        if (customer.getAddresses().size() <= 1) {
            throw new CustomerMustHaveAtLeastOneAddressException("Kunden måste ha minst en adress.");
        }

        // Ta bort adressen från kundens lista
        customer.getAddresses().remove(address);
        // Ta bort kunden från adressens lista
        address.getCustomers().remove(customer);

        customer.getAddresses().remove(address);
        customerRepository.save(customer);

        logger.info("Admin tog bort adress {} på kund {}", address.getId(), customer.getId());

        // Tar bort adressen om den saknar kopplade kunder
        if (address.getCustomers().isEmpty()) {
            addressRepository.delete(address);
            logger.info("Admin tog bort adress {}", address.getId());
        }
    }
}
