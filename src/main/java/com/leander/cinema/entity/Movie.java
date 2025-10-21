package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", unique = true, nullable = false, length = 150)
    private String title;

    @Column(name = "genre", nullable = false, length = 150)
    private String genre;

    @Column(name = "age_limit", nullable = false)
    private int ageLimit;

    @Column(name = "duration", nullable = false)
    private int duration;

//    @OneToMany(mappedBy = "movie")
//    private List<Screening> screenings = new ArrayList<>();
//
//    @OneToMany(mappedBy = "movie")
//    private List<Booking> bookings = new ArrayList<>();

    public Movie() {
    }

    public Movie(String title, String genre, int ageLimit, int duration) {
        this.title = title;
        this.genre = genre;
        this.ageLimit = ageLimit;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

//    public List<Screening> getScreenings() {
//        return screenings;
//    }
//
//    public void setScreenings(List<Screening> screenings) {
//        this.screenings = screenings;
//    }
//
//    public List<Booking> getBookings() {
//        return bookings;
//    }
//
//    public void setBookings(List<Booking> bookings) {
//        this.bookings = bookings;
//    }
}
