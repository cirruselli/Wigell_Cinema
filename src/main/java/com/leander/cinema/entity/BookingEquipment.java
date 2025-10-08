package com.leander.cinema.entity;

import jakarta.persistence.*;

//För att uppdatera rummets/lokalens uppsättning av teknisk utrustning i Booking-klassen
@Entity
@Table(name = "booking_equipment")
public class BookingEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

}
