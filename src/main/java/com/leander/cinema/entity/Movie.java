package com.leander.cinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre", nullable = false)
    private String genre;

    @Column(name = "age_limit", nullable = false)
    private int ageLimit;

    @Column(name = "duration", nullable = false)
    private double duration;
}
