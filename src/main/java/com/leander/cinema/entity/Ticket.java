package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "price_sek", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSek;

    @Column(name = "price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;
}
