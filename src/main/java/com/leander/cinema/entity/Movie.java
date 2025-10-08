package com.leander.cinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "genre", nullable = false, length = 50)
    private String genre;

    @Column(name = "age_limit", nullable = false)
    private int ageLimit;

    @Column(name = "duration", nullable = false)
    private double duration;

    public Movie() {
    }

    public Movie(String title, String genre, int ageLimit, double duration) {
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
