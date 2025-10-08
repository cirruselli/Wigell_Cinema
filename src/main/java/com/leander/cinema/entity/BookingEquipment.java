package com.leander.cinema.entity;

import jakarta.persistence.*;

//För att uppdatera rummets/lokalens uppsättning av teknisk utrustning i Booking-klassen
@Entity
@Table(name = "booking_equipment")
public class BookingEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    public BookingEquipment() {
    }

    public BookingEquipment(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
