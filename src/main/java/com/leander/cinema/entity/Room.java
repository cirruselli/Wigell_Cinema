package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "technical_equipments", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment")
    private List<String> equipments = new ArrayList<>(Arrays.asList("Mikrofon", "Högtalare", "Projektor"));

    public Room() {
    }

    public Room(String name, int maxGuests) {
        this.name = name;
        this.maxGuests = maxGuests;
    }

    public Room(String name, int maxGuests, List<String> equipments) {
        this.name = name;
        this.maxGuests = maxGuests;
        this.equipments = equipments;
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

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
    }
}
