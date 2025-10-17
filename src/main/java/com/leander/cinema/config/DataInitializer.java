package com.leander.cinema.config;

import com.leander.cinema.entity.*;
import com.leander.cinema.repository.*;
import com.leander.cinema.security.AppUser;
import com.leander.cinema.security.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {
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
                AppUser admin1 = new AppUser(
                        "admin1",
                        passwordEncoder.encode("admin1"),
                        Set.of(Role.ADMIN)
                );

                AppUser admin2 = new AppUser(
                        "admin2",
                        passwordEncoder.encode("admin2"),
                        Set.of(Role.ADMIN)
                );

                AppUser superAdmin = new AppUser(
                        "superadmin",
                        passwordEncoder.encode("superadmin"),
                        Set.of(Role.ADMIN, Role.USER)
                );

                appUserRepository.save(admin1);
                appUserRepository.save(admin2);
                appUserRepository.save(superAdmin);
                System.out.println("Skapat adminkonto admin1, admin2, superadmin. Password samma som username");


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
                addressRepository.save(address2);
                addressRepository.save(address3);
                addressRepository.save(address4);

                Customer customer1 = new Customer("Elli", "Malmström", "example@gmail.com", "0754345432", List.of(address1, address3));
                Customer customer2 = new Customer("Max", "Levin", "maxis@gmail.com", "0723456765", List.of(address2, address4));
                Customer customer3 = new Customer("Åsa", "Wallmark", "asa.wallis@gmail.com", "0743234543", List.of(address2));
                Customer customer4 = new Customer("Kajsa", "Ström", "kajsans@example.com", "0734543212", List.of(address3));
                Customer customer5 = new Customer("Emil", "Levin", "emil@example.com", "0778695043", List.of(address4));


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
                customerRepository.save(customer2);
                customerRepository.save(customer3);
                customerRepository.save(customer4);
                customerRepository.save(customer5);

                Movie movie1 = new Movie("Inception", "Sci-Fi", 15, 148);
                Movie movie2 = new Movie("The Lion King", "Animation", 7, 88);
                Movie movie3 = new Movie("Avengers: Endgame", "Action", 11, 181);
                Movie movie4 = new Movie("Mr.Bean", "Comedy", 7, 175);
                Movie movie5 = new Movie("Frozen", "Animation", 7, 102);

                movieRepository.save(movie1);
                movieRepository.save(movie2);
                movieRepository.save(movie3);
                movieRepository.save(movie4);
                movieRepository.save(movie5);

                Room room1 = new Room("Salong A", 200, new BigDecimal("5000.00"), new BigDecimal("500.00"), List.of("Ljudanläggning", "Screen"));
                Room room2 = new Room("Salong B", 150, new BigDecimal("2000.00"), new BigDecimal("200.00"), List.of("Ljudanläggning", "Screen"));
                Room room3 = new Room("Stora konferensrummet", 50, new BigDecimal("1500.00"), new BigDecimal("150.00"), List.of("Whiteboard", "Projektor"));
                Room room4 = new Room("Naturscenen", 1000, new BigDecimal("20000.00"), new BigDecimal("2000.00"), List.of("Naturscen", "Projektor"));
                Room room5 = new Room("Hörnan", 500, new BigDecimal("15000.00"), new BigDecimal("1500.00"), List.of("Högtalare", "Scen", "Scenbelysning", "Mikrofon", "Projektor"));

                roomRepository.save(room1);
                roomRepository.save(room2);
                roomRepository.save(room3);
                roomRepository.save(room4);
                roomRepository.save(room5);

                Screening screening1 = new Screening(
                        LocalDateTime.of(2025, 10, 20, 18, 0),
                        LocalDateTime.of(2025, 10, 20, 21, 0),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room1,
                        movie1
                );

                Screening screening2 = new Screening(
                        LocalDateTime.of(2025, 10, 21, 15, 0),
                        LocalDateTime.of(2025, 10, 21, 18, 0),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room2,
                        movie2
                );

                Screening screening3 = new Screening(
                        LocalDateTime.of(2025, 10, 22, 20, 0),
                        LocalDateTime.of(2025, 10, 22, 23, 0),
                        new BigDecimal("120.00"),
                        new BigDecimal("12.00"),
                        room1,
                        movie3
                );

                Screening screening4 = new Screening(
                        LocalDateTime.of(2025, 10, 23, 17, 30),
                        LocalDateTime.of(2025, 10, 23, 20, 30),
                        new BigDecimal("200.00"),
                        new BigDecimal("20.00"),
                        room2,
                        movie4
                );

                screeningRepository.save(screening1);
                screeningRepository.save(screening2);
                screeningRepository.save(screening3);
                screeningRepository.save(screening4);


                Booking booking1 = new Booking(
                        LocalDateTime.of(2025, 10, 25, 18, 0),
                        LocalDateTime.of(2025, 10, 25, 20, 30),
                        "Sara Karlsson",
                        20,
                        new BigDecimal("12000.00"),
                        new BigDecimal("1200.00"),
                        room1,
                        customer1
                );

                Booking booking2 = new Booking(
                        LocalDateTime.of(2025, 10, 26, 15, 0),
                        LocalDateTime.of(2025, 10, 26, 17, 0),
                        "Johan Glans",
                        150,
                        new BigDecimal("60000.00"),
                        new BigDecimal("6000.00"),
                        room2,
                        customer2
                );

                bookingRepository.save(booking1);
                bookingRepository.save(booking2);


            }
        };
    }
}
