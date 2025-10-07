package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "booking_date")
    private LocalDate bookingDate;
    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY) // Eager fetching kan vara okej här, men om du ofta hämtar bokningar utan kunddata kan LAZY vara bättre.
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_price_SEK", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceSek;
    @Column(name = "total_price_USD", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceUsd;

    public Booking() {
    }


}
