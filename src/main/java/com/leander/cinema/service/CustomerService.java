package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressResponseDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingUpdateRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerWithAccountRequestDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketUpdateRequestDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.exception.AddressAlreadyExistsException;
import com.leander.cinema.exception.BookingConflictException;
import com.leander.cinema.exception.CustomerMustHaveAtLeastOneAddressException;
import com.leander.cinema.exception.CustomerOwnershipException;
import com.leander.cinema.mapper.AddressMapper;
import com.leander.cinema.mapper.CustomerMapper;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import com.leander.cinema.security.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,
                           AddressRepository addressRepository,
                           TicketRepository ticketRepository,
                           ScreeningRepository screeningRepository,
                           BookingRepository bookingRepository,
                           RoomRepository roomRepository,
                           AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
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
        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }


    // === Uppdatera kund ===
    @Transactional
    public AdminCustomerResponseDto updateCustomer(Long id, AdminCustomerWithAccountRequestDto requestDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + id + " hittades inte"));

        // --- Uppdatera kundens primitiva fält
        CustomerMapper.updateCustomer(customer, requestDto);

        // --- Uppdatera adresser ---
        List<Address> updatedAddresses = new ArrayList<>();
        for (AdminAddressRequestDto addressDto : requestDto.addresses()) {

            String street = addressDto.street().trim();
            String postalCode = addressDto.postalCode().trim();
            String city = addressDto.city().trim();

            Address existingAddress = addressRepository.findByStreetAndPostalCodeAndCity(street, postalCode, city);

            if (existingAddress != null) {
                /*Kontrollera om kunden har adressen ->
                 om inte så sätt till existerande adress från databasen
               */
                if (!updatedAddresses.contains(existingAddress)) {
                    updatedAddresses.add(existingAddress);
                }
            } else {
                Address newAddress = new Address();
                newAddress.setStreet(addressDto.street());
                newAddress.setPostalCode(addressDto.postalCode());
                newAddress.setCity(addressDto.city());
                addressRepository.save(newAddress);
                updatedAddresses.add(newAddress);
            }
        }

        customer.setAddresses(updatedAddresses);

        // --- Uppdatera Tickets ---

        if (requestDto.tickets() != null) {
            List<Ticket> updatedTickets = new ArrayList<>();
            for (AdminTicketUpdateRequestDto ticketDto : requestDto.tickets()) {

                Ticket ticket = ticketRepository.findById(ticketDto.ticketId())
                        .orElseThrow(() -> new EntityNotFoundException("Biljett med id " + ticketDto.ticketId() + " hittades inte"));

                //Kontrollera att biljetten tillhör kunden
                if (!ticket.getCustomer().getId().equals(customer.getId())) {
                    throw new CustomerOwnershipException("Biljett med id " + ticketDto.ticketId() + " tillhör inte kunden");
                }

                // --- Hantera Screening vs Booking ---
                if (ticketDto.screeningId() != null && ticketDto.bookingId() != null) {
                    throw new IllegalArgumentException("Endast en av föreställning eller bokning får sättas på samma biljett");
                }

                if (ticketDto.screeningId() != null) {
                    Screening screening = screeningRepository.findById(ticketDto.screeningId())
                            .orElseThrow(() -> new EntityNotFoundException("Föreställning hittades inte"));
                    ticket.setScreening(screening);
                    ticket.setBooking(null); // ta bort koppling till talar-bokning
                } else if (ticketDto.bookingId() != null) {
                    Booking booking = bookingRepository.findById(ticketDto.bookingId())
                            .orElseThrow(() -> new EntityNotFoundException("Bokning hittades inte"));
                    ticket.setBooking(booking);
                    ticket.setScreening(null); // ta bort koppling till film
                }

                ticket.setNumberOfTickets(ticketDto.numberOfTickets());

                //Räknar bara om totalbeloppet då enhetspriset ska vara låst vid uppdatering!
                ticket.setTotalPriceSek(ticket.getPriceSek()
                        .multiply(BigDecimal.valueOf(ticket.getNumberOfTickets())));
                ticket.setTotalPriceUsd(ticket.getPriceUsd()
                        .multiply(BigDecimal.valueOf(ticket.getNumberOfTickets())));

                ticket.setCustomer(customer);

                if (!updatedTickets.contains(ticket)) {
                    updatedTickets.add(ticket);
                }
            }

            customer.getTickets().clear();
            customer.getTickets().addAll(updatedTickets);
        }

        // --- Uppdatera Booking ---

        if (requestDto.bookings() != null) {
            List<Booking> updatedBookings = new ArrayList<>();
            for (AdminBookingUpdateRequestDto bookingDto : requestDto.bookings()) {

                Booking booking = bookingRepository.findById(bookingDto.bookingId())
                        .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + bookingDto.bookingId() + " hittades inte"));

                //Kontrollera att bokningen tillhör kunden
                if (!booking.getCustomer().getId().equals(customer.getId())) {
                    throw new CustomerOwnershipException(
                            "Bokning med id " + bookingDto.bookingId() + " tillhör inte kunden med id " + customer.getId());
                }

                // --- Uppdatera rum ---
                if (bookingDto.roomId() != null) {
                    Room room = roomRepository.findById(bookingDto.roomId())
                            .orElseThrow(() -> new EntityNotFoundException("Lokal hittades inte"));
                    booking.setRoom(room);
                }

                // --- Kontrollera överlapp med andra bokningar och screenings ---
                Room roomToCheck;

                // Bestäm vilket rum som faktiskt ska användas
                if (bookingDto.roomId() != null) {
                    roomToCheck = roomRepository.findById(bookingDto.roomId())
                            .orElseThrow(() -> new EntityNotFoundException("Lokal hittades inte"));
                } else if (bookingDto.screeningId() != null) {
                    Screening screening = screeningRepository.findById(bookingDto.screeningId())
                            .orElseThrow(() -> new EntityNotFoundException("Föreställning hittades inte"));
                    roomToCheck = screening.getRoom();
                } else {
                    roomToCheck = booking.getRoom(); // fallback
                }

                if (roomToCheck != null) {

                    // --- Kontrollera andra bokningar ---
                    List<Booking> roomBookings = bookingRepository.findByRoomId(roomToCheck.getId());
                    for (Booking otherBooking : roomBookings) {
                        if (!otherBooking.getId().equals(booking.getId())) { // exkludera sig själv
                            boolean overlap = bookingDto.reservationStartTime().isBefore(otherBooking.getReservationEndTime())
                                    && bookingDto.reservationEndTime().isAfter(otherBooking.getReservationStartTime());
                            if (overlap) {
                                throw new BookingConflictException("Tiderna för bokningen överlappar med en annan bokning i samma rum.");
                            }
                        }
                    }

                    // --- Kontrollera screenings ---
                    List<Screening> roomScreenings = screeningRepository.findByRoomId((roomToCheck.getId()));
                    for (Screening screening : roomScreenings) {
                        // Exkludera den screening som är kopplad till denna bokning, om någon
                        if (booking.getScreening() != null && screening.getId().equals(booking.getScreening().getId())) {
                            continue;
                        }
                        boolean overlap = bookingDto.reservationStartTime().isBefore(screening.getEndTime())
                                && bookingDto.reservationEndTime().isAfter(screening.getStartTime());
                        if (overlap) {
                            throw new BookingConflictException("Tiderna för bokningen överlappar med en föreställning i samma rum.");
                        }
                    }
                }


                // Kontrollera att sluttid inte är före starttid
                if (bookingDto.reservationEndTime() != null && bookingDto.reservationStartTime() != null
                        && bookingDto.reservationEndTime().isBefore(bookingDto.reservationStartTime())) {
                    throw new IllegalArgumentException("Sluttiden för bokningen kan inte vara före starttiden.");
                }

                // --- Hantera Screening vs Speaker ---
                if (bookingDto.screeningId() != null && bookingDto.speakerName() != null && !bookingDto.speakerName().isBlank()) {
                    throw new IllegalArgumentException("Endast en av föreställning eller talare får sättas på samma bokning");
                }

                // --- Hantera Screening vs Speaker om endast en är satt ---
                if (bookingDto.screeningId() != null) {
                    Screening screening = screeningRepository.findById(bookingDto.screeningId())
                            .orElseThrow(() -> new EntityNotFoundException("Föreställning hittades inte"));
                    booking.setScreening(screening);
                    booking.setSpeakerName(null); // Ta bort talare
                } else if (bookingDto.speakerName() != null && !bookingDto.speakerName().isBlank()) {
                    booking.setSpeakerName(bookingDto.speakerName());
                    booking.setScreening(null); // Ta bort film
                }

                booking.setReservationStartTime(bookingDto.reservationStartTime());
                booking.setReservationEndTime(bookingDto.reservationEndTime());
                booking.setNumberOfGuests(bookingDto.numberOfGuests());

                // --- Kontrollera att antal gäster inte överstiger rummets kapacitet ---
                int maxGuests = booking.getRoom().getMaxGuests();
                if (bookingDto.numberOfGuests() > maxGuests) {
                    throw new IllegalArgumentException("Antalet gäster (" + bookingDto.numberOfGuests() +
                            ") överstiger rummets maxkapacitet (" + maxGuests + ").");
                }

                booking.setStatus(bookingDto.bookingStatus());
                booking.setCustomer(customer);

                if (!updatedBookings.contains(booking)) {
                    updatedBookings.add(booking);
                }
            }
            customer.getBookings().clear();
            customer.getBookings().addAll(updatedBookings);
        }

        // --- Uppdatera AppUser ---
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

        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }

    // === Ta bort kund ===
    @Transactional
    public boolean deleteCustomer(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);

        if (customer.isPresent()) {
            Customer customerEntity = customer.get();
            // Tar bort kopplingarna mellan kunden och adressen i mellantabellen (customer_addresses)
            customerEntity.getAddresses().clear();
            // Tar bort kunden från tabellen customers
            customerRepository.delete(customerEntity);

            // Skickar delete av relationerna till DB innan rensning av adresserna i adress-tabellen
            customerRepository.flush();

            List<Address> allAddresses = addressRepository.findAll();
            for (Address address : allAddresses) {
                // Tar bort alla adresser som saknar kopplade kunder
                if (address.getCustomers().isEmpty()) {
                    addressRepository.delete(address);
                }
            }
            return true;
        } else {
            return false;
        }
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
        }

        if (customer.getAddresses().contains(address)) {
            throw new AddressAlreadyExistsException("Kunden har redan denna adress");
        }

        customer.getAddresses().add(address);
        customerRepository.save(customer);

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

        // Tar bort alla adresser som saknar kopplade kunder
        if (address.getCustomers().isEmpty()) {
            addressRepository.delete(address);
        }
    }

}
