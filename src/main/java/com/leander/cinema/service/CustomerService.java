package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingUpdateRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerRequestDto;
import com.leander.cinema.dto.AdminDto.customerDto.AdminCustomerResponseDto;
import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketUpdateRequestDto;
import com.leander.cinema.entity.*;
import com.leander.cinema.mapper.CustomerMapper;
import com.leander.cinema.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public CustomerService(CustomerRepository customerRepository,
                           AddressRepository addressRepository,
                           TicketRepository ticketRepository,
                           ScreeningRepository screeningRepository,
                           BookingRepository bookingRepository,
                           RoomRepository roomRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

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

    @Transactional
    public AdminCustomerResponseDto createCustomer(AdminCustomerRequestDto body) {
        Customer customer = CustomerMapper.toCustomerEntity(body);

        List<Address> addresses = new ArrayList<>();

        for (AdminAddressRequestDto addressDto : body.addresses()) {

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
                newAddress.setStreet(addressDto.street());
                newAddress.setPostalCode(addressDto.postalCode());
                newAddress.setCity(addressDto.city());
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

        customerRepository.save(customer);
        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }

    @Transactional
    public AdminCustomerResponseDto updateCustomer(Long id, AdminCustomerRequestDto requestDto) {
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

                Ticket ticket = ticketRepository.findById(ticketDto.id())
                        .orElseThrow(() -> new EntityNotFoundException("Biljett med id " + ticketDto.id() + " hittades inte"));

                ticket.setNumberOfTickets(ticketDto.numberOfTickets());

                //***********KONTROLLERA BILJETTPRIS OCH BOKNINGSPRIS! *******************************
                //SEK
                // Totalpris för biljetter = antal biljetter * biljettpris
                BigDecimal ticketTotalSek = ticket.getPriceSek().multiply(BigDecimal.valueOf(ticket.getNumberOfTickets()));
                //USD
                BigDecimal factor = new BigDecimal("0.11");
                //Enskilt biljettpris i USD
                BigDecimal ticketPriceUsd = ticket.getPriceSek().multiply(factor);
                //Totalpris i USD
                BigDecimal ticketTotalUsd = ticketTotalSek.multiply(factor);

                ticket.setTotalPriceSek(ticketTotalSek);
                ticket.setPriceUsd(ticketPriceUsd);
                ticket.setTotalPriceUsd(ticketTotalUsd);

                Screening ticketScreening = screeningRepository.findById(ticketDto.screeningId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Föreställning med id " + ticketDto.screeningId() + " hittades inte"));
                ticket.setScreening(ticketScreening);

                ticket.setCustomer(customer);

                if(!updatedTickets.contains(ticket)) {
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

                Booking booking = bookingRepository.findById(bookingDto.id())
                        .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + bookingDto.id() + " hittades inte"));

                booking.setReservationStartTime(bookingDto.reservationStartTime());
                booking.setReservationEndTime(bookingDto.reservationEndTime());
                booking.setNumberOfGuests(bookingDto.numberOfGuests());

                Room room = roomRepository.findById(bookingDto.roomId())
                        .orElseThrow(() -> new EntityNotFoundException("Rum med id " + bookingDto.roomId() + " hittades inte"));
                booking.setRoom(room);

                Screening screening = screeningRepository.findById(bookingDto.screeningId())
                        .orElseThrow(() -> new EntityNotFoundException("Föreställning med id " + bookingDto.screeningId() + " hittades inte"));
                booking.setScreening(screening);

                // Beräkna pris för hela salongen
                BigDecimal bookingPriceSek = room.getPriceSek().add(screening.getPriceSek());
                booking.setTotalPriceSek(bookingPriceSek);
                booking.setTotalPriceUsd(bookingPriceSek.multiply(new BigDecimal("0.11")));

                booking.setCustomer(customer);

                if(!updatedBookings.contains(booking)) {
                    updatedBookings.add(booking);
                }
            }
            customer.getBookings().clear();
            customer.getBookings().addAll(updatedBookings);
        }

        customerRepository.save(customer);

        return CustomerMapper.toAdminCustomerResponseDto(customer);
    }


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
                if (address.getCustomers() == null || address.getCustomers().isEmpty()) {
                    addressRepository.delete(address);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
