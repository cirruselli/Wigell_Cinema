package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    //Utrustning för rummet vid en patch
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "booking_equipments", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "equipment")
    private List<String> equipments = new ArrayList<>();

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    //RELATION
    @OneToOne(fetch = FetchType.LAZY)
    //Unique = true -> en föreställning kan bara ha en bokning -> enligt krav på att bokning avser hela salongen
    @JoinColumn(name = "screening_id", nullable = false, unique = true)
    private Screening screening;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_price_SEK", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceSek;

    @Column(name = "total_price_USD", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceUsd;

    public Booking() {
    }

    //bookingEquipment är inte med här pga ska inte kunna fyllas i vid en POST -> rummets existerande utrustning ska däremot visas i responsen!
    public Booking(LocalDateTime reservationTime, int numberOfGuests, Room room, Screening screening, Customer customer, BigDecimal totalPriceSek, BigDecimal totalPriceUsd) {
        this.reservationTime = reservationTime;
        this.numberOfGuests = numberOfGuests;
        this.room = room;
        this.screening = screening;
        this.customer = customer;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
    }

    public Booking(LocalDateTime reservationTime, int numberOfGuests, List<String> equipments, Room room, Screening screening, Customer customer, BigDecimal totalPriceSek, BigDecimal totalPriceUsd) {
        this.reservationTime = reservationTime;
        this.numberOfGuests = numberOfGuests;
        this.equipments = equipments;
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

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
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
