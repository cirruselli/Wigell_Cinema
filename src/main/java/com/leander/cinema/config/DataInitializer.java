package com.leander.cinema.config;

import com.leander.cinema.entity.*;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import com.leander.cinema.security.Role;
import com.leander.cinema.service.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {
    Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Transactional
    CommandLineRunner initData(AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder,
                               AddressRepository addressRepository,
                               CustomerRepository customerRepository,
                               MovieRepository movieRepository,
                               BookingRepository bookingRepository,
                               RoomRepository roomRepository,
                               ScreeningRepository screeningRepository) {
        return args -> {
            if (appUserRepository.count() == 0) {
                AppUser admin = new AppUser(
                        "admin",
                        passwordEncoder.encode("admin"),
                        Set.of(Role.ADMIN)
                );

                AppUser admin2 = new AppUser(
                        "admin2",
                        passwordEncoder.encode("admin2"),
                        Set.of(Role.ADMIN)
                );

                appUserRepository.save(admin);
                logger.info("admin skapades");
                appUserRepository.save(admin2);
                logger.info("admin2 skapades");


                AppUser appUser1 = new AppUser(
                        "user1",
                        passwordEncoder.encode("user1"),
                        Set.of(Role.USER)
                );
                AppUser appUser2 = new AppUser(
                        "user2",
                        passwordEncoder.encode("user2"),
                        Set.of(Role.USER)
                );
                AppUser appUser3 = new AppUser(
                        "user3",
                        passwordEncoder.encode("user3"),
                        Set.of(Role.USER)
                );
                AppUser appUser4 = new AppUser(
                        "user4",
                        passwordEncoder.encode("user4"),
                        Set.of(Role.USER)
                );
                AppUser appUser5 = new AppUser(
                        "user5",
                        passwordEncoder.encode("user5"),
                        Set.of(Role.USER)
                );

                Address address1 = new Address("Kaptensgatan 11C", "39243", "Kalmar");
                Address address2 = new Address("Stockholmsvägen 22A", "87592", "Sundsvall");
                Address address3 = new Address("Arontorpsvägen 33B", "76483", "Färjestaden");
                Address address4 = new Address("Hästhovsvägen 22B", "77653", "Luleå");
                addressRepository.save(address1);
                logger.info("Address 1 skapades");
                addressRepository.save(address2);
                logger.info("Address 2 skapades");
                addressRepository.save(address3);
                logger.info("Address 3 skapades");
                addressRepository.save(address4);
                logger.info("Address 4 skapades");

                Customer customer1 = new Customer("Elli", "Malmström", "example@gmail.com", "0754345432", new ArrayList<>(List.of(address1, address3)));
                Customer customer2 = new Customer("Max", "Levin", "maxis@gmail.com", "0723456765", new ArrayList<>(List.of(address2, address4)));
                Customer customer3 = new Customer("Åsa", "Wallmark", "asa.wallis@gmail.com", "0743234543", new ArrayList<>(List.of(address2)));
                Customer customer4 = new Customer("Kajsa", "Ström", "kajsans@example.com", "0734543212", new ArrayList<>(List.of(address3)));
                Customer customer5 = new Customer("Emil", "Levin", "emil@example.com", "0778695043", new ArrayList<>(List.of(address4)));


                appUser1.setCustomer(customer1);
                customer1.setAppUser(appUser1);

                appUser2.setCustomer(customer2);
                customer2.setAppUser(appUser2);

                appUser3.setCustomer(customer3);
                customer3.setAppUser(appUser3);

                appUser4.setCustomer(customer4);
                customer4.setAppUser(appUser4);

                appUser5.setCustomer(customer5);
                customer5.setAppUser(appUser5);

                customerRepository.save(customer1);
                logger.info("Kund och user1 skapades");
                customerRepository.save(customer2);
                logger.info("Kund och user2 skapades");
                customerRepository.save(customer3);
                logger.info("Kund och user3 skapades");
                customerRepository.save(customer4);
                logger.info("Kund och user4 skapades");
                customerRepository.save(customer5);
                logger.info("Kund och user5 skapades");

                Movie movie1 = new Movie("Inception", "Sci-Fi", 15, 148);
                Movie movie2 = new Movie("The Lion King", "Animation", 7, 88);
                Movie movie3 = new Movie("Avengers: Endgame", "Action", 11, 181);
                Movie movie4 = new Movie("Mr.Bean", "Comedy", 7, 175);
                Movie movie5 = new Movie("Frozen", "Animation", 7, 102);

                movieRepository.save(movie1);
                logger.info("Film 1 skapades");
                movieRepository.save(movie2);
                logger.info("Film 2 skapades");
                movieRepository.save(movie3);
                logger.info("Film 3 skapades");
                movieRepository.save(movie4);
                logger.info("Film 4 skapades");
                movieRepository.save(movie5);
                logger.info("Film 5 skapades");

                Room room1 = new Room("Salong A", 200, new BigDecimal("5000.00"), new BigDecimal("500.00"), new ArrayList<>(List.of("Ljudanläggning", "Screen")));
                Room room2 = new Room("Salong B", 150, new BigDecimal("2000.00"), new BigDecimal("200.00"), new ArrayList<>(List.of("Ljudanläggning", "Screen")));
                Room room3 = new Room("Stora konferensrummet", 50, new BigDecimal("1500.00"), new BigDecimal("150.00"), new ArrayList<>(List.of("Whiteboard", "Projektor")));
                Room room4 = new Room("Naturscenen", 1000, new BigDecimal("20000.00"), new BigDecimal("2000.00"), new ArrayList<>(List.of("Naturscen", "Projektor")));
                Room room5 = new Room("Hörnan", 500, new BigDecimal("15000.00"), new BigDecimal("1500.00"), new ArrayList<>(List.of("Högtalare", "Scen", "Scenbelysning", "Mikrofon", "Projektor")));

                roomRepository.save(room1);
                logger.info("Lokal 1 skapades");
                roomRepository.save(room2);
                logger.info("Lokal 2 skapades");
                roomRepository.save(room3);
                logger.info("Lokal 3 skapades");
                roomRepository.save(room4);
                logger.info("Lokal 4 skapades");
                roomRepository.save(room5);
                logger.info("Lokal 5 skapades");

                Screening screening1 = new Screening(
                        LocalDateTime.of(2025, 11, 20, 18, 0),
                        LocalDateTime.of(2025, 11, 20, 21, 0),
                        LocalDateTime.of(2025, 11, 20, 21, 30),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room1,
                        movie1
                );

                Screening screening2 = new Screening(
                        LocalDateTime.of(2025, 11, 21, 15, 0),
                        LocalDateTime.of(2025, 11, 21, 18, 0),
                        LocalDateTime.of(2025, 11, 21, 18, 30),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room2,
                        movie2
                );

                Screening screening3 = new Screening(
                        LocalDateTime.of(2025, 11, 22, 20, 0),
                        LocalDateTime.of(2025, 11, 22, 23, 0),
                        LocalDateTime.of(2025, 11, 22, 23, 30),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room1,
                        movie3
                );

                Screening screening4 = new Screening(
                        LocalDateTime.of(2025, 11, 23, 17, 30),
                        LocalDateTime.of(2025, 11, 23, 20, 30),
                        LocalDateTime.of(2025, 11, 23, 21, 0),
                        new BigDecimal("200.00"),
                        new BigDecimal("20.00"),
                        room2,
                        movie4
                );

                screeningRepository.save(screening1);
                logger.info("Föreställning 1 skapades");
                screeningRepository.save(screening2);
                logger.info("Föreställning 2 skapades");
                screeningRepository.save(screening3);
                logger.info("Föreställning 3 skapades");
                screeningRepository.save(screening4);
                logger.info("Föreställning 4 skapades");


                Booking booking1 = new Booking(
                        LocalDateTime.of(2025, 10, 30, 18, 0),
                        LocalDateTime.of(2025, 10, 30, 20, 30),
                        "Johan Glans",
                        null,
                        500,
                        new ArrayList<>(List.of("Mikrofon", "Mikrofon", "Scenbelysning", "Projektor", "Dator", "Högtalare")),
                        new BigDecimal("12000.00"),
                        new BigDecimal("1200.00"),
                        BookingStatus.ACTIVE,
                        room5,
                        customer1
                );

                Booking booking2 = new Booking(
                        LocalDateTime.of(2025, 10, 30, 15, 0),
                        LocalDateTime.of(2025, 10, 30, 17, 0),
                        null,
                        movie4,
                        150,
                        new ArrayList<>(List.of("Mikrofon", "Scenbelysning", "Högtalare")),
                        new BigDecimal("60000.00"),
                        new BigDecimal("6000.00"),
                        BookingStatus.ACTIVE,
                        room2,
                        customer2
                );

                Booking booking3 = new Booking(
                        LocalDateTime.of(2025, 11, 20, 15, 0),
                        LocalDateTime.of(2025, 11, 20, 17, 0),
                        null,
                        movie5,
                        150,
                        new ArrayList<>(List.of("Mikrofon", "Scenbelysning", "Högtalare")),
                        new BigDecimal("60000.00"),
                        new BigDecimal("6000.00"),
                        BookingStatus.ACTIVE,
                        room2,
                        customer3
                );

                bookingRepository.save(booking1);
                logger.info("Bokning 1 skapades");
                bookingRepository.save(booking2);
                logger.info("Bokning 2 skapades");
                bookingRepository.save(booking3);
                logger.info("Bokning 3 skapades");
            }
        };
    }
}
