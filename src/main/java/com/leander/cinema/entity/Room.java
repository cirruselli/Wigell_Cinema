package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "max_guests", nullable = false)
    private int maxGuests;

    //RELATION
    //Standard-utrustning för rummet
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEquipment> technicalEquipmentList = new ArrayList<>();

    public Room() {
    }

    public Room(String name, int maxGuests) {
        this.name = name;
        this.maxGuests = maxGuests;
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

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public List<RoomEquipment> getTechnicalEquipmentList() {
        return technicalEquipmentList;
    }

    public void setTechnicalEquipmentList(List<RoomEquipment> technicalEquipmentList) {
        this.technicalEquipmentList = technicalEquipmentList;
    }
}
