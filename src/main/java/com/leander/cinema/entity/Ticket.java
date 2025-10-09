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
    @JoinColumn(name = "room_id")
    private Room room;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "price_sek", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSek;

    @Column(name = "price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;

    public Ticket() {
    }

    public Ticket(BigDecimal priceSek, BigDecimal priceUsd) {
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
    }

    public Ticket(Customer customer, Screening screening, BigDecimal priceSek, BigDecimal priceUsd) {
        this.customer = customer;
        this.screening = screening;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Screening getScreening() {
        return screening;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public BigDecimal getPriceSek() {
        return priceSek;
    }

    public void setPriceSek(BigDecimal priceSek) {
        this.priceSek = priceSek;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }
}
