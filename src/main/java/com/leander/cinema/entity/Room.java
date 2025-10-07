package com.leander.cinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int maxGuests;
    @Column(nullable = false)
    private String technicalEquipment;
}
