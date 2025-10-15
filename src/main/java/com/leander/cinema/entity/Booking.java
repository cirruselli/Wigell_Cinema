package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_start_time")
    private LocalDateTime reservationStartTime;

    @Column(name = "reservation_end_time")
    private LocalDateTime reservationEndTime;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

//    //Utrustning för rummet vid en patch
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "equipments", joinColumns = @JoinColumn(name = "booking_id"))
//    @Column(name = "equipment")
//    private List<String> equipment = new ArrayList<>();

    @Column(name = "total_price_SEK", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceSek;

    @Column(name = "total_price_USD", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceUsd;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;



    public Booking() {
    }

    public Booking(LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, int numberOfGuests, BigDecimal totalPriceSek, BigDecimal totalPriceUsd) {
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
        this.numberOfGuests = numberOfGuests;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
    }

    public Booking(LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, int numberOfGuests, BigDecimal totalPriceSek, BigDecimal totalPriceUsd, Room room, Screening screening, Customer customer) {
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
        this.numberOfGuests = numberOfGuests;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
        this.room = room;
        this.screening = screening;
        this.customer = customer;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getReservationStartTime() {
        return reservationStartTime;
    }

    public void setReservationStartTime(LocalDateTime reservationStartTime) {
        this.reservationStartTime = reservationStartTime;
    }

    public LocalDateTime getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(LocalDateTime reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
}
