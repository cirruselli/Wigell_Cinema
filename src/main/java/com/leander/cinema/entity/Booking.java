package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    //För att uppdatera teknisk utrustning på enskild bokning
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingEquipment> bookingEquipmentList;

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

    public Booking(LocalDate bookingDate, int numberOfGuests, Room room, Screening screening, Customer customer, BigDecimal totalPriceSek, BigDecimal totalPriceUsd) {
        this.bookingDate = bookingDate;
        this.numberOfGuests = numberOfGuests;
        this.room = room;
        this.screening = screening;
        this.customer = customer;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<BookingEquipment> getBookingEquipmentList() {
        return bookingEquipmentList;
    }

    public void setBookingEquipmentList(List<BookingEquipment> bookingEquipmentList) {
        this.bookingEquipmentList = bookingEquipmentList;
    }

    public Screening getScreening() {
        return screening;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
}
