package com.leander.cinema.entity;

import jakarta.persistence.*;

//Standard-utrustning för rum/lokal i Room-klassen
@Entity
@Table(name = "room_equipment")
public class RoomEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
