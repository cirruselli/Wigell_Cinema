package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_of_tickets", nullable = false)
    private int numberOfTickets;

    @Column(name = "price_sek", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSek;

    @Column(name = "price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;

    @Column(name = "total_price_sek", nullable = false)
    private BigDecimal totalPriceSek;

    @Column(name = "total_price_usd", nullable = false)
    private BigDecimal totalPriceUsd;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    //RELATION
    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    public Ticket() {
    }

    public Ticket(int numberOfTickets, BigDecimal priceSek, BigDecimal priceUsd, BigDecimal totalPriceSek, BigDecimal totalPriceUsd) {
        this.numberOfTickets = numberOfTickets;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
    }

    public Ticket(int numberOfTickets, BigDecimal priceSek, BigDecimal priceUsd, BigDecimal totalPriceSek, BigDecimal totalPriceUsd, Customer customer, Screening screening) {
        this.numberOfTickets = numberOfTickets;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
        this.customer = customer;
        this.screening = screening;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
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

    public BigDecimal getTotalPriceSek() {
        return totalPriceSek;
    }

    public void setTotalPriceSek(BigDecimal totalPriceSek) {
        this.totalPriceSek = totalPriceSek;
    }

    public BigDecimal getTotalPriceUsd() {
        return totalPriceUsd;
    }

    public void setTotalPriceUsd(BigDecimal totalPriceUsd) {
        this.totalPriceUsd = totalPriceUsd;
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
}
