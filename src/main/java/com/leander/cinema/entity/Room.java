package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "max_guests", nullable = false)
    private int maxGuests;

    //RELATION
    //Standard-utrustning för rummet
    //Fungerar som mall vid en PATCH
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEquipment> technicalEquipmentList;
}
