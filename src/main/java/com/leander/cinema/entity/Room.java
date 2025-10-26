package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "max_guests", nullable = false)
    private int maxGuests;

    @Column(name = "price_sek", nullable = false)
    private BigDecimal priceSek;

    @Column(name = "price_usd", nullable = false)
    private BigDecimal priceUsd;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "standard_equipments", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment")
    private List<String> standardEquipment = new ArrayList<>();

    public Room() {
    }

    public Room(String name, int maxGuests, BigDecimal priceSek, List<String> standardEquipment) {
        this.name = name;
        this.maxGuests = maxGuests;
        this.priceSek = priceSek;
        this.standardEquipment = standardEquipment;
    }

    public Room(String name, int maxGuests, BigDecimal priceSek, BigDecimal priceUsd, List<String> standardEquipment) {
        this.name = name;
        this.maxGuests = maxGuests;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.standardEquipment = standardEquipment;
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

    public BigDecimal getPriceSek() {
        return priceSek;
    }

    public void setPriceSek(BigDecimal priceSek) {
        this.priceSek = priceSek;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public List<String> getStandardEquipment() {
        return standardEquipment;
    }

    public void setStandardEquipment(List<String> standardEquipment) {
        this.standardEquipment = standardEquipment;
    }
}