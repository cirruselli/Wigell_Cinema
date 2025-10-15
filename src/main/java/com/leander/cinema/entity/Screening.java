package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "screenings")
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "price_sek", nullable = false)
    private BigDecimal priceSek;

    @Column(name = "price_usd", nullable = false)
    private BigDecimal priceUsd;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaker_id")
    private Speaker speaker;

    public Screening() {
    }

    public Screening(LocalDateTime startTime, BigDecimal priceSek, BigDecimal priceUsd, Room room, Speaker speaker) {
        this.startTime = startTime;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.room = room;
        this.speaker = speaker;
    }

    public Screening(LocalDateTime startTime, BigDecimal priceSek, BigDecimal priceUsd, Room room, Movie movie) {
        this.startTime = startTime;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.room = room;
        this.movie = movie;
    }

    public Screening(LocalDateTime startTime, BigDecimal priceSek, BigDecimal priceUsd, Room room) {
        this.startTime = startTime;
        this.priceSek = priceSek;
        this.priceUsd = priceUsd;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }
}
