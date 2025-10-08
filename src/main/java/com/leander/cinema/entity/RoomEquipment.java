package com.leander.cinema.entity;

import jakarta.persistence.*;

//Standard-utrustning för rum/lokal i Room-klassen
@Entity
@Table(name = "room_equipment")
public class RoomEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public RoomEquipment() {
    }

    public RoomEquipment(String name, Room room) {
        this.name = name;
        this.room = room;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
